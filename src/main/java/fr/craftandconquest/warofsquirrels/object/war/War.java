package fr.craftandconquest.warofsquirrels.object.war;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.channels.WarChannel;
import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.*;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

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

    @Getter private final UUID uuid;

    @Getter @Setter private String tag;
    @Getter @Setter private City cityAttacker;
    @Getter @Setter private City cityDefender;
    @Getter @Setter private Territory targetTerritory;
    @Getter @Setter private List<FullPlayer> attackers;
    @Getter @Setter private List<FullPlayer> defenders;
    @Getter @Setter private Timer timer;
    @Getter @Setter private WarState state;
    @Getter @Setter private int attackersLimit;
    @Getter @Setter private long timeStart;
    @Getter @Setter private FullPlayer target;
    @Getter @Setter private boolean targetAlreadyDead = false;
    @Getter @Setter private Score attackersPoints;
    @Getter @Setter private Score defendersPoints;

    @Getter @Setter private int lastAnnounceCapture = 0;
    @Getter @Setter private float captureSpeed;

    private final List<Chunk> capturedChunk = new ArrayList<>();
    private final Map<Chunk, Pair<Float, Float>> chunkBeingCaptured = new HashMap<>();

    private Scoreboard scoreboard;
    private PlayerTeam attackerTeam;
    private PlayerTeam defenderTeam;
    private Objective timerObjective;
    private Objective pointsObjective;

    private final List<Chunk> warChunks = new ArrayList<>();
    private final List<FullPlayer> glowPlayers = new ArrayList<>();
    public War(City attacker, City defender, Territory territory, List<FullPlayer> attackersParty) {
        uuid = UUID.randomUUID();
        cityAttacker = attacker;
        cityDefender = defender;
        targetTerritory = territory;
        target = null;
        attackers = new ArrayList<>();
        defenders = new ArrayList<>();
        tag = SetTag();
        state = WarState.Preparation;

        SetWarChunks();

        SetScoreboard();

        WarOfSquirrels.instance.getBroadCastHandler().AddTarget(this, new WarChannel(this));
        defender.getOnlinePlayers().forEach(this::AddDefender);
        attackersLimit = defenders.size() + 1;
        attackersParty.forEach(this::AddAttacker);

        MutableComponent worldAnnounce = new TextComponent("The war horns roar. ");
        MutableComponent attackerName = ChatText.Colored(attacker.displayName, ChatFormatting.BLUE);
        MutableComponent defenderName = ChatText.Colored(defender.displayName, ChatFormatting.RED);
        worldAnnounce.append(attackerName).append(" is attacking ").append(defenderName).append(" on territory ").append(targetTerritory.getName());

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(worldAnnounce);

        SetTarget();
        LaunchPreparation();
    }

    public void SetWarChunks() {
        warChunks.addAll(WarOfSquirrels.instance.getChunkHandler().getChunks(targetTerritory.getFortification()));
    }

    public void SetScoreboard() {
        scoreboard = new Scoreboard();

        timerObjective = scoreboard.addObjective("time", ObjectiveCriteria.DUMMY,
                ChatText.Colored("Time left", ChatFormatting.GOLD),
                ObjectiveCriteria.RenderType.INTEGER);

        pointsObjective = scoreboard.addObjective("points", ObjectiveCriteria.DUMMY,
                ChatText.Colored("Points", ChatFormatting.YELLOW),
                ObjectiveCriteria.RenderType.INTEGER);

        attackersPoints = scoreboard.getOrCreatePlayerScore(cityAttacker.tag, pointsObjective);
        defendersPoints = scoreboard.getOrCreatePlayerScore(cityDefender.tag, pointsObjective);
        attackersPoints.setScore(0);
        defendersPoints.setScore(0);

        attackerTeam = scoreboard.addPlayerTeam(cityAttacker.displayName);
        defenderTeam = scoreboard.addPlayerTeam(cityDefender.displayName);

        attackerTeam.setAllowFriendlyFire(false);
        attackerTeam.setPlayerPrefix(ChatText.Colored("Att", ChatFormatting.RED));
        defenderTeam.setAllowFriendlyFire(false);
        defenderTeam.setPlayerPrefix(ChatText.Colored("Def", ChatFormatting.BLUE));

        defenders.forEach(f -> {
            f.getPlayerEntity().getScoreboard().setDisplayObjective(Scoreboard.DISPLAY_SLOT_TEAMS_SIDEBAR_START, timerObjective);
            f.getPlayerEntity().getScoreboard().setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, pointsObjective);
        });
        attackers.forEach(f -> {
            f.getPlayerEntity().getScoreboard().setDisplayObjective(Scoreboard.DISPLAY_SLOT_TEAMS_SIDEBAR_START, timerObjective);
            f.getPlayerEntity().getScoreboard().setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, pointsObjective);
        });
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

    public void AddAttacker(FullPlayer player) {
        if (attackers.size() == attackersLimit) {
            player.sendMessage(ChatText.Error("You can't join this war, wait for defenders to join"));
            return;
        }
        attackers.add(player);
        scoreboard.addPlayerToTeam(player.getDisplayName(), attackerTeam);
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(this, player);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                ChatText.Colored(player.getDisplayName() + " join as attacker ! ["
                        + attackers.size() + "/" + attackersLimit + "]", ChatFormatting.GOLD), true);

        player.getPlayerEntity().getScoreboard().setDisplayObjective(Scoreboard.DISPLAY_SLOT_TEAMS_SIDEBAR_START, timerObjective);
        player.getPlayerEntity().getScoreboard().setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, pointsObjective);
    }

    public void AddDefender(FullPlayer player) {
        defenders.add(player);
        ++attackersLimit;
        scoreboard.addPlayerToTeam(player.getDisplayName(), defenderTeam);
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(this, player);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                ChatText.Colored(player.getDisplayName() + " join as defender ! ["
                        + defenders.size() + "]", ChatFormatting.GOLD), true);

        player.getPlayerEntity().getScoreboard().setDisplayObjective(Scoreboard.DISPLAY_SLOT_TEAMS_SIDEBAR_START, timerObjective);
        player.getPlayerEntity().getScoreboard().setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, pointsObjective);
    }

    public void AddPoints(int points, Score total) {
        int score = total.getScore();

        score = Math.min(score + points, 1000);

        total.setScore(score);
    }

    public void AddDefenderPoints(int points) {
        AddPoints(points, defendersPoints);
    }

    public void AddAttackerPoints(int points) {
        AddPoints(points, attackersPoints);
    }

    public void AddDefenderKillPoints() {
        AddDefenderPoints(166 / (3 * attackers.size()));
    }

    public void AddAttackerKillPoints() {
        AddAttackerPoints(166 / (3 * defenders.size()));
    }

    public void AddAttackerTargetKillPoints() {
        if (!targetAlreadyDead) {
            AddAttackerPoints(650);
            targetAlreadyDead = true;
        } else {
            AddAttackerKillPoints();
        }
    }

    public void ForceWinAttacker() {
        AddAttackerPoints(1000);
    }

    public void ForceWinDefender() {
        AddDefenderPoints(1000);
    }

    public void AddAttackerCapturePoints() {
        int max = targetTerritory.getFortification().getMaxChunk() - 1;
        int used = WarOfSquirrels.instance.getChunkHandler().getSize(targetTerritory.getFortification()) - 1;
        int result = Math.round(max <= 15 ? 1001f / used : 67 * ((float) max / (float) used));

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                ChatText.Colored("Chunk captured for a total of " + result + " points.", ChatFormatting.GOLD), true);
        AddAttackerPoints(result);
    }

    private void LaunchPreparation() {
        this.timer = new Timer();
        this.timeStart = System.currentTimeMillis();

        WarOfSquirrels.instance.getBroadCastHandler().WarAnnounce(this, WarState.Preparation);

        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                PreLaunchWar();
            }
        }, WarOfSquirrels.instance.getConfig().getPreparationPhase() * 1000L);
    }

    private void PreLaunchWar() {
        this.state = WarState.War;
        this.LaunchWar();
    }

    private void LaunchWar() {
        WarTask warTask = new WarTask();

        warTask.setWar(this);
        Capture();
        this.timer.cancel();
        WarOfSquirrels.instance.getBroadCastHandler().WarAnnounce(this, WarState.War);
        this.timer = new Timer();
        this.timeStart = System.currentTimeMillis();
        timer.schedule(warTask, 0, 1000);
    }

    public void LaunchRollback() {
        WarOfSquirrels.instance.getBroadCastHandler().WarAnnounce(this, WarState.Rollback);

        DeclareWinner();
        ClearScoreboard(scoreboard);

        defenders.forEach(f -> ClearScoreboard(f.getPlayerEntity().getScoreboard()));
        attackers.forEach(f -> ClearScoreboard(f.getPlayerEntity().getScoreboard()));

        defenders.clear();
        attackers.clear();

        this.state = WarState.Rollback;
        this.timer.cancel();

        WarOfSquirrels.instance.getWarHandler().delete(this);
    }

    protected void DeclareWinner() {
        BroadCastHandler broadCastHandler = WarOfSquirrels.instance.getBroadCastHandler();
        MutableComponent text;
        MutableComponent influenceLost;
        IChannelTarget influenceMessageTarget = null;
        boolean hasDefenseWon = defendersPoints.getScore() >= 1000;

        int pointsToRemove = 250 + Math.abs(defendersPoints.getScore() - attackersPoints.getScore());

        targetTerritory.setGotAttackedToday(true);

        influenceLost = ChatText.Colored("Your faction lost " + pointsToRemove + " influence points on territory '", ChatFormatting.DARK_RED);

        if (hasDefenseWon) {
            text = ChatText.Colored(cityAttacker.displayName
                    + " failed to win his attack against "
                    + cityDefender.displayName
                    + "(" + attackersPoints.getScore() + " - " + defendersPoints.getScore(), ChatFormatting.GOLD);

            Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(cityAttacker);
            WarOfSquirrels.instance.getInfluenceHandler().get(territory.getFaction(), territory).SubInfluence(pointsToRemove);

            influenceLost.append(territory.getName() + "'");
            influenceMessageTarget = cityAttacker.getFaction();
        } else {
            text = ChatText.Colored(cityAttacker.displayName
                    + " won his attack against "
                    + cityDefender.displayName
                    + "(" + attackersPoints.getScore() + " - " + defendersPoints.getScore(), ChatFormatting.GOLD);

            Influence influence = WarOfSquirrels.instance.getInfluenceHandler().get(targetTerritory.getFaction(), targetTerritory);
            influence.SubInfluence(pointsToRemove + WarOfSquirrels.instance.getTerritoryHandler().getDamageFromEnemy(targetTerritory, cityAttacker.getFaction()));

            targetTerritory.setGotDefeatedToday(true);

            if (influence.getValue() <= 0) {
                text.append(ChatText.Colored("\n Faction " + cityAttacker.getFaction().getDisplayName() + " lost all his influence on territory '" + targetTerritory.getName() + "' and territory has fall.", ChatFormatting.GOLD));
                targetTerritory.setHasFallen(true);
                targetTerritory.setDaysBeforeReset(3);
            }
            influenceLost.append(targetTerritory + "'");
        }

        broadCastHandler.BroadCastWorldAnnounce(text);
        broadCastHandler.BroadCastMessage(influenceMessageTarget, null, influenceLost, true);
    }

    protected boolean CheckWin() {
        if (defendersPoints.getScore() >= 1000 || attackersPoints.getScore() >= 1000) {
            this.state = WarState.Rollback;
            return true;
        }
        return false;
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
        MutableComponent message = new TextComponent("===| " + this.tag + " |===\n");

        message.append(ChatText.Colored("Attackers [" + cityAttacker.displayName + "] : " + Utils.getStringFromPlayerList(attackers), ChatFormatting.BLUE));
        message.append(ChatText.Colored("\nDefenders [" + cityDefender.displayName + "] : " + Utils.getStringFromPlayerList(defenders), ChatFormatting.RED));
        message.append(ChatText.Colored("\nTarget : " + this.target.getDisplayName(), ChatFormatting.GOLD));
        message.append(ChatText.Colored("\nPhase : " + this.GetPhase() + " (time left : " + this.TimeLeft() + ")", ChatFormatting.WHITE));
        message.append(ChatText.Colored("\n===[" + cityAttacker.tag + "] "
                + attackersPoints.getScore() + " - " + defendersPoints.getScore() + " [" + cityDefender.tag + "]===", ChatFormatting.WHITE).withStyle(ChatFormatting.BOLD));

        player.sendMessage(message);

        chunkBeingCaptured.forEach((chunk, pair) -> {
            if (pair.getValue() != 0f)
                player.sendMessage(ChatText.Success(chunk.toStringShort() + "[~"
                        + (chunk.getPosX() * 16) + ";~"
                        + (chunk.getPosZ() * 16) + "][" + (int) Math.floor(pair.getKey()) + "/100] "
                        + Utils.toTime((int) (pair.getKey() / pair.getValue()))));
        });
    }

    private void Capture() {
        int chunkMax = targetTerritory.getFortification().getMaxChunk() - 1;
        float time = (chunkMax <= 15.0f ? 900.0f / chunkMax : 60f);

        captureSpeed = (100f / time);
    }

    private float Capture(int attOnChunk, int defOnChunk) {
        float attRatio = attOnChunk / (float) attackers.size();
        float defRatio = defOnChunk / (float) defenders.size();

        return captureSpeed * (attRatio - (0.42f * defRatio));
    }

    protected void UpdateCapture() {
        List<FullPlayer> att = new ArrayList<>(attackers);
        List<FullPlayer> def = new ArrayList<>(defenders);

        att.forEach(this::CleanGlowPlayer);
        def.forEach(this::CleanGlowPlayer);

        for (Chunk chunk : warChunks) {
            int attOnChunk = 0;
            int defOnChunk = 0;

            if (att.size() > 0) {
                for (int i = att.size() - 1; i >= 0; --i) {
                    if (att.get(i).getLastChunkX() == chunk.getPosX() && att.get(i).getLastChunkZ() == chunk.getPosZ()) {
                        ++attOnChunk;
                        GlowPlayer(att.get(i));
                        att.remove(i);
                    }
                }
            }

            if (def.size() > 0) {
                for (int i = def.size() - 1; i >= 0; --i) {
                    if (def.get(i).getLastChunkX() == chunk.getPosX() && att.get(i).getLastChunkZ() == chunk.getPosZ()) {
                        ++defOnChunk;
                        GlowPlayer(def.get(i));
                        def.remove(i);
                    }
                }
            }

            if (attOnChunk == 0 && defOnChunk == 0) continue;

            float valueToRemove = Capture(attOnChunk, defOnChunk);

            Pair<Float, Float> newPair = new Pair<>(100f, valueToRemove);

            if (chunkBeingCaptured.containsKey(chunk)) {
                newPair.setKey(Utils.clamp(chunkBeingCaptured.get(chunk).getKey() - valueToRemove, 0, 100));
                chunkBeingCaptured.replace(chunk, newPair);
            } else {
                newPair.setKey(Utils.clamp(100f - valueToRemove, 0, 100));
                chunkBeingCaptured.put(chunk, newPair);
                lastAnnounceCapture = 0;
            }

            if (this.lastAnnounceCapture == 0 && chunkBeingCaptured.get(chunk) != null) {
                float timeLeft = chunkBeingCaptured.get(chunk).getKey(); // example : 55
                if (valueToRemove >= 0) {
                    WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                            ChatText.Success("[Capture]" + chunk.toStringShort() + "[" + (int) Math.floor(timeLeft) + "/100] Time before capture "
                                    + Utils.toTime((int) (timeLeft / valueToRemove)) + "."),
                            true);
                }
            }

            if (chunkBeingCaptured.get(chunk) != null && chunkBeingCaptured.get(chunk).getKey() <= 0f) {
                capturedChunk.add(chunk);
                warChunks.remove(chunk);
                chunkBeingCaptured.remove(chunk);
                AddAttackerCapturePoints();
            }
        }

        if (this.lastAnnounceCapture == 0)
            this.lastAnnounceCapture = 30;
        this.lastAnnounceCapture -= 1;
    }

    private void GlowPlayer(FullPlayer player) {
        player.getPlayerEntity().addEffect(new MobEffectInstance(MobEffects.GLOWING, 2));
        if (!glowPlayers.contains(player))
            glowPlayers.add(player);
    }

    private void CleanGlowPlayer(FullPlayer player) {
        if (glowPlayers.contains(player))
            player.getPlayerEntity().removeEffect(MobEffects.GLOWING);
    }

    public void UpdateTimer(int secondsLeft) {
        String secondsAsTime = Utils.toTime(secondsLeft);

        timerObjective.setDisplayName(ChatText.Colored("Time left : " + secondsAsTime, ChatFormatting.GOLD));
    }

    public Vector3 getAttackerSpawn(FullPlayer player) {
        if (player.getCity().equals(cityAttacker))
            return WarOfSquirrels.instance.getChunkHandler().getSpawnOnTerritory(targetTerritory, cityAttacker);
        else
            return WarOfSquirrels.instance.getChunkHandler().getSpawnOnTerritory(targetTerritory, player.getCity());
    }

    public Vector3 getDefenderSpawn(FullPlayer player) {
        if (player.getCity().equals(cityDefender) || player.getCity().getCityUpgrade().getHeadQuarter().getCurrentLevel() >= 3)
            return WarOfSquirrels.instance.getChunkHandler().getHomeBlock(targetTerritory.getFortification()).getRespawnPoint();
        else
            return WarOfSquirrels.instance.getChunkHandler().getSpawnOnTerritory(targetTerritory, player.getCity());
    }

    public void ClearScoreboard(Scoreboard board) {
        board.removeObjective(timerObjective);
        board.removeObjective(pointsObjective);
        board.removePlayerTeam(attackerTeam);
        board.removePlayerTeam(defenderTeam);
    }

    public boolean isCaptured(Vector3 worldPos) {
        Vector2 chunkPos = Utils.FromWorldToChunk((int) worldPos.x, (int) worldPos.z);

        for (Chunk chunk : capturedChunk) {
            if (chunk.getPosX() == chunkPos.x && chunk.getPosZ() == chunkPos.y) return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        War war = (War) obj;

        return war.getUuid().equals(this.uuid);
    }
}
