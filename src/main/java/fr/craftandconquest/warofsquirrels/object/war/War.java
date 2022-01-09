package fr.craftandconquest.warofsquirrels.object.war;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.channels.WarChannel;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.faction.city.CityRank;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.world.BlockEvent;

import java.util.*;

public class War implements IChannelTarget {
    @Override
    public BroadCastTarget getBroadCastTarget() {
        return BroadCastTarget.WAR;
    }

    public enum WarState {
        Declaration,
        Preparation,
        War,
        Rollback
    }

    @Getter
    @Setter
    private String tag;
    @Getter
    @Setter
    private City cityAttacker;
    @Getter
    @Setter
    private City cityDefender;
    @Getter
    @Setter
    private List<FullPlayer> attackers;
    @Getter
    @Setter
    private List<FullPlayer> defenders;
    @Getter
    @Setter
    private Timer timer;
    @Getter
    @Setter
    private WarState state;
    @Getter
    @Setter
    private int attackersLimit;
    @Getter
    @Setter
    private long timeStart;
    @Getter
    @Setter
    private FullPlayer target;
    @Getter
    @Setter
    private boolean targetDead = false;
    @Getter
    @Setter
    private Score attackersPoints;
    @Getter
    @Setter
    private Score defendersPoints;

    @Getter
    @Setter
    private int lastAnnounceCapture = 0;
    @Getter
    @Setter
    private float captureSpeed;

    private final List<Chunk> capturedChunk = new ArrayList<>();
    private final Map<Chunk, Float> chunkBeingCaptured = new HashMap<>();

    private Scoreboard scoreboard;
    private PlayerTeam attackerTeam;
    private PlayerTeam defenderTeam;
    private Objective timerObjective;

    public War(City attacker, City defender, List<FullPlayer> attackersParty) {
        cityAttacker = attacker;
        cityDefender = defender;
        target = null;
        attackers = new ArrayList<>();
        defenders = new ArrayList<>();
        SetScoreboard();
        WarOfSquirrels.instance.getBroadCastHandler().AddTarget(this, new WarChannel(this));
        defender.getOnlinePlayers().forEach(this::AddDefender);
        attackersLimit = defenders.size() + 1;
        attackersParty.forEach(this::AddAttacker);
        state = WarState.Preparation;
        tag = SetTag();

        MutableComponent worldAnnounce = new TextComponent("The war horns roar. ");
        MutableComponent attackerName = ChatText.Colored(attacker.displayName, ChatFormatting.BLUE);
        MutableComponent defenderName = ChatText.Colored(defender.displayName, ChatFormatting.RED);
        worldAnnounce.append(attackerName).append(" is attacking ").append(defenderName);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(worldAnnounce);

        SetTarget();
        LaunchPreparation();
    }

    public void SetScoreboard() {
        scoreboard = new Scoreboard();

        timerObjective = scoreboard.addObjective(
                "timerObjective",
                ObjectiveCriteria.DUMMY,
                ChatText.Colored("Time left", ChatFormatting.GOLD),
                ObjectiveCriteria.RenderType.INTEGER);
        scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, timerObjective);

        Objective attackersObjective = scoreboard.addObjective(
                "attackersObjective",
                ObjectiveCriteria.DUMMY,
                ChatText.Colored(cityAttacker.displayName, ChatFormatting.BLUE),
                ObjectiveCriteria.RenderType.INTEGER);
        scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, attackersObjective);

        attackersPoints = scoreboard.getOrCreatePlayerScore("attackersPoints", attackersObjective);
        attackersPoints.setScore(0);

        Objective defendersObjective = scoreboard.addObjective(
                "defendersObjective",
                ObjectiveCriteria.DUMMY,
                ChatText.Colored(cityDefender.displayName, ChatFormatting.RED),
                ObjectiveCriteria.RenderType.INTEGER);
        scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, defendersObjective);

        defendersPoints = scoreboard.getOrCreatePlayerScore("defendersPoints", defendersObjective);
        defendersPoints.setScore(0);

        attackerTeam = scoreboard.addPlayerTeam(cityAttacker.displayName);
        defenderTeam = scoreboard.addPlayerTeam(cityDefender.displayName);

        attackerTeam.setAllowFriendlyFire(false);
        defenderTeam.setAllowFriendlyFire(false);
    }

    private void SetTarget() {
        for (FullPlayer defender : defenders) {
            if (defender.getCity().getOwner() == defender) {
                target = defender;
                return;
            } else if (defender.getAssistant() && target == null) {
                target = defender;
            }
        }

        if (target == null) target = defenders.get(0);
    }

    private String SetTag() {
        return cityAttacker.displayName.substring(0, 3) +
                cityDefender.displayName.substring(0, 3);
    }

    public boolean AddAttacker(FullPlayer player) {
        if (attackers.size() == attackersLimit) {
            player.sendMessage(ChatText.Error("You can't join this war, wait for defenders to join"));
            return false;
        }
        attackers.add(player);
        scoreboard.addPlayerToTeam(player.getDisplayName(), attackerTeam);
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(this, player);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                ChatText.Colored(player.getDisplayName() + " join as attacker ! ["
                        + attackers.size() + "/" + attackersLimit + "]", ChatFormatting.GOLD), true);
        return true;
    }

    public boolean AddDefender(FullPlayer player) {
        defenders.add(player);
        ++attackersLimit;
        scoreboard.addPlayerToTeam(player.getDisplayName(), defenderTeam);
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(this, player);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                ChatText.Colored(player.getDisplayName() + " join as defender ! ["
                        + defenders.size() + "]", ChatFormatting.GOLD), true);
        return true;
    }

    public void AddRollbackBlock(BlockEvent.BreakEvent event) {
        // Nothing for the moment
    }

    public void AddDefenderPoints(int points) {
        this.defendersPoints.add(points);
    }

    public void AddDefenderKillPoints() {
        this.defendersPoints.add(166 / (3 * attackers.size()));
    }

    public void AddAttackerKillPoints() {
        this.attackersPoints.add(166 / (3 * defenders.size()));
    }

    public void AddAttackerTargetKillPoints() {
        if (!targetDead) {
            this.attackersPoints.add(650);
            targetDead = true;
        } else {
            AddAttackerKillPoints();
        }
    }

    public void ForceWinAttacker() {
        this.attackersPoints.add(1000);
    }

    public void ForceWinDefender() {
        this.defendersPoints.add(1000);
    }

    public int AddAttackerCapturePoints() {
        CityRank rank = cityDefender.getRank();
        int max = rank.getChunkMax();
        int used = WarOfSquirrels.instance.getChunkHandler().getSize(cityDefender);
        int result = Math.round(max <= 15 ? 1000f / used : 67 * ((float) max / (float) used));

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                ChatText.Colored("Chunk captured for a total of " + result + " points.", ChatFormatting.GOLD), true);
        attackersPoints.add(result);
        return result;
    }

    private void LaunchPreparation() {
        this.timer = new Timer();

        WarOfSquirrels.instance.getBroadCastHandler().WarAnnounce(this, WarState.Preparation);

        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                this.cancel();
                PreLaunchWar();
            }
        }, WarOfSquirrels.instance.getConfig().getPreparationPhase() * 1000);
    }

    private void PreLaunchWar() {
        this.state = WarState.War;
        this.LaunchWar();
    }

    private void LaunchWar() {
        WarTask warTask = new WarTask();

        WarOfSquirrels.instance.getBroadCastHandler().WarAnnounce(this, WarState.War);
        warTask.setWar(this);
        Capture();
        this.timer = new Timer();
        timer.schedule(warTask, 0, 1000);
    }

    public void LaunchRollback() {
        this.timer = new Timer();
        WarOfSquirrels.instance.getBroadCastHandler().WarAnnounce(this, WarState.Rollback);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Rollback();
            }
        }, WarOfSquirrels.instance.getConfig().getRollbackPhase() * 1000L);
    }

    public void Rollback() {
        // Nothing to do for now
    }

    protected boolean CheckWin() {
        BroadCastHandler broadCastHandler = WarOfSquirrels.instance.getBroadCastHandler();
        MutableComponent text;
        if (defendersPoints.getScore() >= 1000)
            text = ChatText.Colored(cityAttacker.displayName
                    + " failed to win his attack against "
                    + cityDefender.displayName
                    + "(" + attackersPoints.getScore() + " - " + defendersPoints.getScore(), ChatFormatting.GOLD);
        else if (attackersPoints.getScore() >= 1000)
            text = ChatText.Colored(cityAttacker.displayName
                    + " won his attack against "
                    + cityDefender.displayName
                    + "(" + attackersPoints.getScore() + " - " + defendersPoints.getScore(), ChatFormatting.GOLD);
        else
            return false;
        broadCastHandler.BroadCastWorldAnnounce(text);
        this.state = WarState.Rollback;
        return true;
    }

    public boolean contains(FullPlayer player) {
        return (attackers.contains(player) || defenders.contains(player));
    }

    public boolean contains(City city) {
        return cityDefender == city || cityAttacker == city;
    }

    private String TimeLeft() {
        long time = System.currentTimeMillis();
        long timeLeft, delta;
        double elapsedSeconds;
        int minutes, seconds;

        String res = "";

        delta = time - timeStart;
        if (state == WarState.Preparation)
            timeLeft = (long) ((WarOfSquirrels.instance.getConfig().getPreparationPhase()) * 1000.0);
        else if (state == WarState.War)
            timeLeft = (long) ((30.0 * 60.0) * 1000.0);
        else
            timeLeft = (long) (WarOfSquirrels.instance.getConfig().getRollbackPhase() * 1000.0);
        timeLeft = timeLeft - delta;
        elapsedSeconds = timeLeft / 1000.0;
        minutes = (int) (elapsedSeconds / 60);
        seconds = (int) (elapsedSeconds % 60);
        if (minutes > 0)
            res = minutes + " min ";
        res += seconds + " s";

        return res;
    }

    public void ForceAttackerWin() {
        attackersPoints.setScore(1000);
    }

    public void ForceDefenderWin() {
        defendersPoints.setScore(1000);
    }

    public String GetPhase() {
        if (state == WarState.Preparation)
            return "Preparation";
        else if (state == WarState.War)
            return "War";
        else
            return "Rollback";
    }

    public List<FullPlayer> GetProtagonists() {
        List<FullPlayer> list = new ArrayList<>(defenders);

        list.addAll(attackers);
        return list;
    }

    public boolean RemovePlayer(FullPlayer player) {
        City city = player.getCity();

        if (attackers.contains(player))
            attackers.remove(player);
        else if (defenders.contains(player) && city != cityDefender) {
            defenders.remove(player);
            --attackersLimit;
        } else
            return false;
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                ChatText.Colored(player.getDisplayName()
                        + " left the battlefield [" + attackers.size() + "/" + attackersLimit + "]", ChatFormatting.GOLD),
                true);
        return true;
    }

    public boolean isTarget(FullPlayer victim) {
        return target.equals(victim);
    }

    public boolean isAttacker(FullPlayer player) {
        return attackers.contains(player);
    }

    public boolean isDefender(FullPlayer player) {
        return defenders.contains(player);
    }

    public List<String> getAttackersAsString() {
        List<String> list = new ArrayList<>();

        attackers.forEach(a -> list.add(a.getDisplayName()));
        return list;
    }

    public List<String> getDefendersAsString() {
        List<String> list = new ArrayList<>();

        defenders.forEach(d -> list.add(d.getDisplayName()));
        return list;
    }

    public void displayInfo(FullPlayer player) {
        MutableComponent message = new TextComponent("===| " + this.tag + " |===");

        message.append(ChatText.Colored("Attackers [" + cityAttacker.displayName + "] : " + Utils.getStringFromPlayerList(attackers), ChatFormatting.BLUE));
        message.append(ChatText.Colored("\nDefenders [" + cityDefender.displayName + "] : " + Utils.getStringFromPlayerList(defenders), ChatFormatting.RED));
        message.append(ChatText.Colored("\nTarget : " + this.target.getDisplayName(), ChatFormatting.GOLD));
        message.append(ChatText.Colored("\nPhase : " + this.GetPhase() + " (time left : " + this.TimeLeft() + ")", ChatFormatting.WHITE));
        message.append(ChatText.Colored("\n===[" + cityAttacker.tag + "] "
                + attackersPoints.getScore() + " - " + defendersPoints.getScore() + " [" + cityDefender.tag + "]===", ChatFormatting.WHITE).withStyle(ChatFormatting.BOLD));

        player.sendMessage(message);

        chunkBeingCaptured.forEach((chunk, f) -> {
            float val = Capture(chunk);

            if (val > 0f)
                player.sendMessage(ChatText.Success("["
                        + (chunk.posX * 16) + ";"
                        + (chunk.posZ * 16) + "] "
                        + Utils.toTime((int) (chunkBeingCaptured.get(chunk) / val))
                        + " secondes."));

        });
    }

    private void Capture() {
        int chunkMax = cityDefender.getRank().getChunkMax() - 1;
        float time = (chunkMax <= 15.0f ? 900.0f / chunkMax : 60f);

        captureSpeed = (100f / (attackers.size() * time));
    }

    private float Capture(Chunk chunk) {
        Level world;
        int att = 0, def = 0, x, z;
        float ret;
        Chunk c;
        List<FullPlayer> list = new ArrayList<>();

        list.addAll(attackers);
        list.addAll(defenders);

        for (FullPlayer player : list) {
            if (player.isOnline()) {
                world = player.getPlayerEntity().getCommandSenderWorld();
                x = (int) player.lastPosition.x;
                z = (int) player.lastPosition.z;

                c = WarOfSquirrels.instance.getChunkHandler().getChunk(x, z, player.getLastDimensionKey());
                if (c != null && c == chunk && inCaptureRange((int) player.lastPosition.y, c.getCity())) {
                    if (isAttacker(player))
                        att++;
                    else if (isDefender(player))
                        def++;
                }
            }
        }

        ret = ((captureSpeed * att) - (0.45f * captureSpeed * def));
        return ret;
    }

    private boolean inCaptureRange(int y, City city) {
        int ySpawn = WarOfSquirrels.instance.getChunkHandler().getHomeBlock(city).getRespawnY();

        return y <= ySpawn + 20 && y <= ySpawn - 20;
    }

    protected void UpdateCapture() {
        List<Chunk> updated = new ArrayList<>();
        Level world;
        float v;

        for (FullPlayer att : attackers) {
            if (att.isOnline()) {
                world = att.getPlayerEntity().getCommandSenderWorld();
                Chunk chunk = WarOfSquirrels.instance.getChunkHandler()
                        .getChunk(att.getLastChunkX(), att.getLastChunkZ(), att.getLastDimensionKey());

                if (chunk != null && chunk.getCity() == cityDefender
                        && !updated.contains(chunk) && !capturedChunk.contains(chunk)
                        && !chunk.getHomeBlock() && !chunk.getOutpost()) {
                    updated.add(chunk);
                    if (chunkBeingCaptured.containsKey(chunk)) {
                        v = chunkBeingCaptured.get(chunk);
                        chunkBeingCaptured.compute(chunk, (c, val) -> val - Capture(chunk));
                    } else {
                        v = 100;
                        chunkBeingCaptured.put(chunk, 100 - Capture(chunk));
                    }
                    if (this.lastAnnounceCapture == 0)
                        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                                ChatText.Success("[Capture][" + (chunk.posX * 16)
                                        + ";" + (chunk.posZ * 16) + "] Time before capture "
                                        + Utils.toTime((int) (chunkBeingCaptured.get(chunk) / (v - chunkBeingCaptured.get(chunk)))) + " seconds."),
                                true);
                    if (chunkBeingCaptured.get(chunk) <= 0) {
                        capturedChunk.add(chunk);
                        chunkBeingCaptured.remove(chunk);
                        AddAttackerCapturePoints();
                    }
                }
            }
        }
        if (this.lastAnnounceCapture == 0)
            this.lastAnnounceCapture = 30;
        this.lastAnnounceCapture -= 1;
    }

    public void UpdateTimer(int secondsLeft) {
        String secondsAsTime = Utils.toTime(secondsLeft);

        timerObjective.setDisplayName(ChatText.Colored("Time left : " + secondsAsTime, ChatFormatting.GOLD));
    }
}
