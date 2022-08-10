package fr.craftandconquest.warofsquirrels.object.war;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.channels.WarChannel;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.*;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

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
    @Getter @Setter private int attackersPoints;
    @Getter @Setter private int defendersPoints;

    @Getter @Setter private int lastAnnounceCapture = 0;
    @Getter @Setter private float captureSpeed;

    private final WarDisplay warDisplay;

    private final List<Chunk> capturedChunk = new ArrayList<>();
    private final Map<Chunk, Pair<Float, Float>> chunkBeingCaptured = new HashMap<>();

    private final List<Chunk> warChunks = new ArrayList<>();

    private int lastGlowPlayer = 15;
    private final List<FullPlayer> attackerCapturing = new ArrayList<>();

    private Timer glowTimer;

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

        warDisplay = new WarDisplay(this);

        WarOfSquirrels.instance.getBroadCastHandler().AddTarget(this, new WarChannel(this));
        defender.getOnlinePlayers().forEach(this::AddDefender);
        attackersLimit = defenders.size() + 1;
        attackersParty.forEach(this::AddAttacker);

        MutableComponent worldAnnounce = ChatText.Colored("The war horns roar. ", ChatFormatting.WHITE);
        MutableComponent attackerName = ChatText.Colored(attacker.getDisplayName(), ChatFormatting.BLUE);
        MutableComponent defenderName = ChatText.Colored(defender.getDisplayName(), ChatFormatting.RED);
        worldAnnounce.append(attackerName).append(" is attacking ").append(defenderName).append(" on territory ").append(targetTerritory.getDisplayName());

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(worldAnnounce);

        SetTarget();
        StartGlowingTimer();
        LaunchPreparation();
    }

    public void StartGlowingTimer() {
        glowTimer = new Timer();
        glowTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (lastGlowPlayer <= 0) {
                    attackerCapturing.forEach(this::GlowPlayer);
                    lastGlowPlayer = 20;
                } else {
                    --lastGlowPlayer;
                }
            }

            private void GlowPlayer(FullPlayer player) {
                player.getPlayerEntity().addEffect(new MobEffectInstance(MobEffects.GLOWING, (int) (8 / WarOfSquirrels.server.getAverageTickTime())));
            }
        }, 0, 1000);
    }
    
    public void SetWarChunks() {
        warChunks.addAll(WarOfSquirrels.instance.getChunkHandler().getChunks(targetTerritory.getFortification()));
        warChunks.removeIf(Chunk::getHomeBlock);
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
        return cityAttacker.getDisplayName().substring(0, 3) +
                cityDefender.getDisplayName().substring(0, 3);
    }

    public void AddAttacker(FullPlayer player) {
        if (attackers.size() == attackersLimit) {
            player.sendMessage(ChatText.Error("You can't join this war, wait for defenders to join"));
            return;
        }
        attackers.add(player);
        warDisplay.AddAttacker(player);
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(this, player);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                ChatText.Colored(player.getDisplayName() + " join as attacker ! ["
                        + attackers.size() + "/" + attackersLimit + "]", ChatFormatting.GOLD), true);
    }

    public void AddDefender(FullPlayer player) {
        defenders.add(player);
        ++attackersLimit;
        warDisplay.AddDefender(player);
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(this, player);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null,
                ChatText.Colored(player.getDisplayName() + " join as defender ! ["
                        + defenders.size() + "]", ChatFormatting.GOLD), true);
    }

    public void AddDefenderPoints(int points) {
        defendersPoints = Math.min(defendersPoints + points, 1000);
        warDisplay.UpdateScore();
    }

    public void AddAttackerPoints(int points) {
        attackersPoints = Math.min(attackersPoints + points, 1000);
        warDisplay.UpdateScore();
    }

    public int ComputeKillPoint(int size) {
        return 500 / (4 * size);
    }

    public void AddDefenderKillPoints() {
        AddDefenderPoints(ComputeKillPoint(attackers.size()));
    }

    public void AddAttackerKillPoints() {
        AddAttackerPoints(ComputeKillPoint(defenders.size()));
    }

    public void AddAttackerTargetKillPoints() {
        if (!targetAlreadyDead) {
            AddAttackerPoints(350);
            targetAlreadyDead = true;
        } else {
            AddAttackerKillPoints();
        }
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
        warDisplay.Clear();

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
        boolean hasDefenseWon = defendersPoints >= 1000;

        int pointsToRemove = 250 + Math.abs(defendersPoints - attackersPoints);

        targetTerritory.setGotAttackedToday(true);
        glowTimer.cancel();

        influenceLost = ChatText.Colored("Your faction lost " + pointsToRemove + " influence points on territory '", ChatFormatting.DARK_RED);

        if (hasDefenseWon) {
            text = ChatText.Colored(cityAttacker.getDisplayName()
                    + " failed to win his attack against "
                    + cityDefender.getDisplayName()
                    + " (" + attackersPoints + " - " + defendersPoints + ")", ChatFormatting.GOLD);

            Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(cityAttacker);
            WarOfSquirrels.instance.getInfluenceHandler().get(territory.getFaction(), territory).SubInfluence(pointsToRemove);

            influenceLost.append(territory.getDisplayName() + "'");
            influenceMessageTarget = cityAttacker.getFaction();
        } else {
            text = ChatText.Colored(cityAttacker.getDisplayName()
                    + " won his attack against "
                    + cityDefender.getDisplayName()
                    + " (" + attackersPoints + " - " + defendersPoints + ")", ChatFormatting.GOLD);

            Influence influence = WarOfSquirrels.instance.getInfluenceHandler().get(targetTerritory.getFaction(), targetTerritory);
            influence.SubInfluence(pointsToRemove + WarOfSquirrels.instance.getTerritoryHandler().getDamageFromEnemy(targetTerritory, cityAttacker.getFaction()));

            targetTerritory.setGotDefeatedToday(true);
            targetTerritory.getFortification().getScore().RemoveScoreLifePoints(Math.abs(defendersPoints - attackersPoints));

            fr.craftandconquest.warofsquirrels.object.scoring.Score attackerFactionScore = cityAttacker.getFaction().getScore();
            fr.craftandconquest.warofsquirrels.object.scoring.Score defenderFactionScore = cityDefender.getFaction().getScore();

            if (attackerFactionScore.getGlobalScore() < defenderFactionScore.getGlobalScore()) {
                float clampDefScore = Math.max(defenderFactionScore.getGlobalScore(), 1f);
                float clampAtkScore = Math.max(attackerFactionScore.getGlobalScore(), 1f);
                float ratioScore = Math.min(3, clampDefScore / clampAtkScore);
                int pointsRound = Math.round((attackersPoints - defendersPoints) * ratioScore);
                int scoreStolen = Math.min(pointsRound, defenderFactionScore.getGlobalScore());
                int scoreSplitFull = Math.round(scoreStolen * 0.65f);
                int scoreSplitReduce = Math.round(scoreStolen * 0.3f);

                for (FullPlayer attacker : attackers) {
                    Faction attackerF = attacker.getCity().getFaction();
                    int scoreGiven;

                    if (attackerF.equals(cityAttacker.getFaction())) {
                        if (attacker.hasPassEnoughTimeInCity()) {
                            scoreGiven = scoreSplitFull;
                        } else {
                            scoreGiven = scoreSplitReduce;
                        }
                    } else
                        scoreGiven = scoreSplitReduce;

                    attacker.getScore().AddScore(scoreGiven);
                    attacker.sendMessage(ChatText.Success("You won '" + scoreGiven + "' score from this war."));
                }

                attackerFactionScore.AddWarScore(scoreStolen);
                defenderFactionScore.AddWarScore(-scoreStolen);
                WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(cityAttacker.getFaction(), null,
                        ChatText.Colored("You stole '" + scoreStolen + "' score from faction '" + cityDefender.getFaction() + "'", ChatFormatting.GOLD), true);
                WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(cityDefender.getFaction(), null,
                        ChatText.Colored("You got stolen '" + scoreStolen + "' score by faction '" + cityAttacker.getFaction() + "'", ChatFormatting.GOLD), true);
            }

            if (influence.getValue() <= 0) {
                text.append(ChatText.Colored("\n Faction " + cityAttacker.getFaction().getDisplayName() + " lost all his influence on territory '" + targetTerritory.getDisplayName() + "' and territory has fall.", ChatFormatting.GOLD));
                targetTerritory.setHasFallen(true);
                targetTerritory.setDaysBeforeReset(3);
            }
            influenceLost.append(targetTerritory + "'");
        }

        broadCastHandler.BroadCastWorldAnnounce(text);
        broadCastHandler.BroadCastMessage(influenceMessageTarget, null, influenceLost, true);
    }

    protected boolean CheckWin() {
        if (defendersPoints >= 1000 || attackersPoints >= 1000) {
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
        AddAttackerPoints(1000);
    }

    public void ForceDefenderWin() {
        AddDefenderPoints(1000);
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

        if (attackers.contains(player)) {
            attackers.remove(player);
            warDisplay.RemoveAttacker(player);
        } else if (defenders.contains(player) && city != cityDefender) {
            defenders.remove(player);
            --attackersLimit;
            warDisplay.RemoveDefender(player);
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

    public List<ServerPlayer> getAttendees() {
        List<ServerPlayer> attendees = new ArrayList<>();

        for (FullPlayer player : attackers) attendees.add((ServerPlayer) player.getPlayerEntity());
        for (FullPlayer player : defenders) attendees.add((ServerPlayer) player.getPlayerEntity());

        return attendees;
    }

    public void displayInfo(FullPlayer player) {
        MutableComponent message = ChatText.Colored("===| " + this.tag + " |===\n", ChatFormatting.WHITE);

        message.append(ChatText.Colored("Attackers [" + cityAttacker.getDisplayName() + "] : " + Utils.getStringFromPlayerList(attackers), ChatFormatting.BLUE));
        message.append(ChatText.Colored("\nDefenders [" + cityDefender.getDisplayName() + "] : " + Utils.getStringFromPlayerList(defenders), ChatFormatting.RED));
        message.append(ChatText.Colored("\nTarget : " + this.target.getDisplayName(), ChatFormatting.GOLD));
        message.append(ChatText.Colored("\nPhase : " + this.GetPhase() + " (time left : " + this.TimeLeft() + ")", ChatFormatting.WHITE));
        message.append(ChatText.Colored("\n===[" + cityAttacker.tag + "] "
                + attackersPoints + " - " + defendersPoints + " [" + cityDefender.tag + "]===", ChatFormatting.WHITE).withStyle(ChatFormatting.BOLD));

        player.sendMessage(message);

        chunkBeingCaptured.forEach((chunk, pair) -> {
            if (pair.getValue() != 0f) {
                float valueToRemove = (pair.getKey() / pair.getValue());
                String toTime = Utils.toTime((int) valueToRemove);

                if (valueToRemove > 0 || pair.getValue() < 100f) {
                    player.sendMessage(ChatText.Success(chunk.toStringShort() + "[~"
                            + (chunk.getPosX() * 16) + ";~"
                            + (chunk.getPosZ() * 16) + "][" + (int) Math.floor(pair.getKey()) + "/100] "
                            + toTime));
                }
            }
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

        attackerCapturing.clear();

        for (Chunk chunk : warChunks) {
            int attOnChunk = 0;
            int defOnChunk = 0;

            if (att.size() > 0) {
                for (int i = att.size() - 1; i >= 0; --i) {
                    if (att.get(i).getCurrentChunk().x == chunk.getPosX() && att.get(i).getCurrentChunk().y == chunk.getPosZ()) {
                        ++attOnChunk;
                        attackerCapturing.add(att.get(i));
                        att.remove(i);
                    }
                }
            }

            if (def.size() > 0) {
                for (int i = def.size() - 1; i >= 0; --i) {
                    if (def.get(i).getCurrentChunk().x == chunk.getPosX() && def.get(i).getCurrentChunk().y == chunk.getPosZ()) {
                        ++defOnChunk;
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

                chunkBeingCaptured.remove(chunk);
                AddAttackerCapturePoints();
            }
        }

        for (Chunk ch: capturedChunk) {
            warChunks.remove(ch);
        }

        if (this.lastAnnounceCapture == 0)
            this.lastAnnounceCapture = 30;
        this.lastAnnounceCapture -= 1;
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

    public void Cancel() {
        warDisplay.Clear();
        getTimer().cancel();
    }
}
