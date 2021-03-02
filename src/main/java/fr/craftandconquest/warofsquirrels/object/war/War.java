package fr.craftandconquest.warofsquirrels.object.war;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.channels.WarChannel;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.faction.city.CityRank;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.scoreboard.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
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

    @Getter @Setter private String tag;
    @Getter @Setter private City cityAttacker;
    @Getter @Setter private City cityDefender;
    @Getter @Setter private List<Player> attackers;
    @Getter @Setter private List<Player> defenders;
    @Getter @Setter private Timer timer;
    @Getter @Setter private WarState state;
    @Getter @Setter private int attackersLimit;
    @Getter @Setter private long timeStart;
    @Getter @Setter private Player target;
    @Getter @Setter private boolean targetDead = false;
    @Getter @Setter private Score attackersPoints;
    @Getter @Setter private Score defendersPoints;

    @Getter @Setter private int lastAnnounceCapture = 0;
    @Getter @Setter private float captureSpeed;

    private final List<Chunk> capturedChunk = new ArrayList<>();
    private final Map<Chunk, Float> chunkBeingCaptured = new HashMap<>();

    private Scoreboard scoreboard;
    private ScorePlayerTeam attackerTeam;
    private ScorePlayerTeam defenderTeam;

    public War(City attacker, City defender, List<Player> attackersParty) {
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

        ITextComponent worldAnnounce = new StringTextComponent("The war horns roar. ");
        ITextComponent attackerName = new StringTextComponent(attacker.displayName).applyTextStyle(TextFormatting.RED);
        ITextComponent defenderName = new StringTextComponent(defender.displayName).applyTextStyle(TextFormatting.RED);
        worldAnnounce.appendSibling(attackerName).appendText(" is attacking ").appendSibling(defenderName);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(worldAnnounce);

        SetTarget();
        LaunchPreparation();
    }

    public void SetScoreboard() {
        scoreboard = new Scoreboard();

        ScoreObjective attackersObjective = scoreboard.addObjective(
                "attackersObjective",
                ScoreCriteria.DUMMY,
                new StringTextComponent(cityAttacker.displayName).applyTextStyle(TextFormatting.RED),
                ScoreCriteria.RenderType.INTEGER);
        scoreboard.setObjectiveInDisplaySlot(1, attackersObjective);

        attackersPoints = scoreboard.getOrCreateScore("attackersPoints", attackersObjective);
        attackersPoints.setScorePoints(0);

        ScoreObjective defendersObjective = scoreboard.addObjective(
                "defendersObjective",
                ScoreCriteria.DUMMY,
                new StringTextComponent(cityDefender.displayName).applyTextStyle(TextFormatting.RED),
                ScoreCriteria.RenderType.INTEGER);
        scoreboard.setObjectiveInDisplaySlot(1, defendersObjective);

        defendersPoints = scoreboard.getOrCreateScore("defendersPoints", defendersObjective);
        defendersPoints.setScorePoints(0);

        attackerTeam = scoreboard.createTeam(cityAttacker.displayName);
        defenderTeam = scoreboard.createTeam(cityDefender.displayName);

        attackerTeam.setAllowFriendlyFire(false);
        defenderTeam.setAllowFriendlyFire(false);
    }

    private void SetTarget() {
        for (Player defender : defenders) {
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

    public boolean AddAttacker(Player player) {
        if (attackers.size() == attackersLimit) {
            player.getPlayerEntity().sendMessage(new StringTextComponent("You can't join this war, wait for defenders to join"));
            return false;
        }
        attackers.add(player);
        scoreboard.addPlayerToTeam(player.getDisplayName(), attackerTeam);
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(this, player);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                new StringTextComponent(player.getDisplayName() + " join as attacker ! ["
                        + attackers.size() + "/" + attackersLimit + "]"), true);
        return true;
    }

    public boolean AddDefender(Player player) {
        defenders.add(player);
        ++attackersLimit;
        scoreboard.addPlayerToTeam(player.getDisplayName(), defenderTeam);
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(this, player);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                new StringTextComponent(player.getDisplayName() + " join as defender ! ["
                        + defenders.size() + "]"), true);
        return true;
    }

    public void AddRollbackBlock(BlockEvent.BreakEvent event) {
        // Nothing for the moment
    }

    public void AddDefenderPoints(int points) {
        this.defendersPoints.increaseScore(points);
    }

    public void AddDefenderKillPoints() {
        this.defendersPoints.increaseScore(166 / (3 * attackers.size()));
    }

    public void AddAttackerKillPoints() {
        this.attackersPoints.increaseScore(166 / (3 * defenders.size()));
    }

    public void AddAttackerTargetKillPoints() {
        if (!targetDead) {
            this.attackersPoints.increaseScore(650);
            targetDead = true;
        } else {
            AddAttackerKillPoints();
        }
    }

    public void ForceWinAttacker() {
        this.attackersPoints.setScorePoints(1000);
    }

    public void ForceWinDefender() {
        this.defendersPoints.setScorePoints(1000);
    }

    public int AddAttackerCapturePoints() {
        CityRank rank = cityDefender.getRank();
        int max = rank.getChunkMax();
        int used = WarOfSquirrels.instance.getChunkHandler().getSize(cityDefender);
        int result = Math.round(max <= 15 ? 1000f / used : 67 * ((float) max / (float) used));

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                new StringTextComponent("Chunk captured for a total of " + result + " points."), true);
        attackersPoints.increaseScore(result);
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
        }, WarOfSquirrels.instance.getConfig().getRollbackPhase() * 1000);
    }

    public void Rollback() {
        // Nothing to do for now
    }

    protected boolean CheckWin() {
        BroadCastHandler broadCastHandler = WarOfSquirrels.instance.getBroadCastHandler();
        StringTextComponent text;
        if (defendersPoints.getScorePoints() >= 1000)
            text = new StringTextComponent(cityAttacker.displayName
                    + " failed to win his attack against "
                    + cityDefender.displayName
                    + "(" + attackersPoints.getScorePoints() + " - " + defendersPoints.getScorePoints());
        else if (attackersPoints.getScorePoints() >= 1000)
            text = new StringTextComponent(cityAttacker.displayName
                    + " won his attack against "
                    + cityDefender.displayName
                    + "(" + attackersPoints.getScorePoints() + " - " + defendersPoints.getScorePoints());
        else
            return false;
        broadCastHandler.BroadCastWorldAnnounce(text);
        this.state = WarState.Rollback;
        return true;
    }

    public boolean contains(Player player) {
        return (attackers.contains(player) || defenders.contains(player));
    }

    public boolean contains(City city) {
        return cityDefender == city || cityAttacker == city;
    }

    private String TimeLeft() {
        long            time = System.currentTimeMillis();
        long            timeLeft, delta;
        double          elapsedSeconds;
        int             minutes, seconds;

        String          res = "";

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
        attackersPoints.setScorePoints(1000);
    }

    public void ForceDefenderWin() {
        defendersPoints.setScorePoints(1000);
    }

    public String GetPhase() {
        if (state == WarState.Preparation)
            return "Preparation";
        else if (state == WarState.War)
            return "War";
        else
            return "Rollback";
    }

    public List<Player> GetProtagonists() {
        List<Player> list = new ArrayList<>(defenders);

        list.addAll(attackers);
        return list;
    }

    public boolean RemovePlayer(Player player) {
        City city = player.getCity();

        if (attackers.contains(player))
            attackers.remove(player);
        else if (defenders.contains(player) && city != cityDefender) {
            defenders.remove(player);
            --attackersLimit;
        } else
            return false;
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                new StringTextComponent(player.getDisplayName()
                        + " left the battlefield [" + attackers.size() + "/" + attackersLimit + "]"),
                true);
        return true;
    }

    public boolean          isTarget(Player victim) { return target.equals(victim); }
    public boolean          isAttacker(Player player) { return attackers.contains(player); }
    public boolean          isDefender(Player player) { return defenders.contains(player); }
    public List<String>     getAttackersAsString() {
        List<String>        list = new ArrayList<>();

        attackers.forEach(a -> list.add(a.getDisplayName()));
        return list;
    }
    public List<String>     getDefendersAsString() {
        List<String>        list = new ArrayList<>();

        defenders.forEach(d -> list.add(d.getDisplayName()));
        return list;
    }

    public void     displayInfo(Player player) {
        player.getPlayerEntity().sendMessage(new StringTextComponent("===| " + this.tag + " |==="));
        player.getPlayerEntity().sendMessage(new StringTextComponent("Attackers [" + cityAttacker.displayName + "] : " + Utils.getStringFromPlayerList(attackers)).applyTextStyle(TextFormatting.RED));
        player.getPlayerEntity().sendMessage(new StringTextComponent("Defenders [" + cityDefender.displayName + "] : " + Utils.getStringFromPlayerList(defenders)).applyTextStyle(TextFormatting.BLUE));
        player.getPlayerEntity().sendMessage(new StringTextComponent("\nTarget : " + this.target.getDisplayName()).applyTextStyle(TextFormatting.GOLD));
        player.getPlayerEntity().sendMessage(new StringTextComponent("Phase : " + this.GetPhase() + " (time left : " + this.TimeLeft() + ")"));
        player.getPlayerEntity().sendMessage(new StringTextComponent("\n===[" + cityAttacker.tag + "] "
                + attackersPoints.getScorePoints() + " - " + defendersPoints.getScorePoints() + " [" + cityDefender.tag + "]===").applyTextStyle(TextFormatting.BOLD));
        chunkBeingCaptured.forEach((chunk, f) -> {
            float val = Capture(chunk);

            if (val > 0f)
                player.getPlayerEntity().sendMessage(new StringTextComponent("["
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
        World world;
        int att = 0, def = 0, x, z;
        float ret;
        Chunk c;
        List<Player> list = new ArrayList<>();

        list.addAll(attackers);
        list.addAll(defenders);

        for (Player player : list) {
            if (player.isOnline()) {
                world = player.getPlayerEntity().getEntityWorld();
                x = (int) player.lastPosition.x;
                z = (int) player.lastPosition.z;

                c = WarOfSquirrels.instance.getChunkHandler().getChunk(x, z, player.lastDimension.getId());
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

        return y <=  ySpawn + 20 && y <= ySpawn - 20;
    }

    protected void UpdateCapture() {
        List<Chunk>         updated = new ArrayList<>();
        World               world;
        float               v;

        for (Player att : attackers) {
            if (att.isOnline()) {
                world = att.getPlayerEntity().getEntityWorld();
                Chunk chunk = WarOfSquirrels.instance.getChunkHandler()
                        .getChunk(att.getLastChunkX(), att.getLastChunkZ(), att.lastDimension.getId());

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
                                new StringTextComponent("[Capture][" + (chunk.posX * 16)
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
}
