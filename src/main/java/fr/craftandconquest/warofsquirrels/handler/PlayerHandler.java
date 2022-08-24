package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.FakePlayer;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class PlayerHandler extends UpdatableHandler<FullPlayer> {
    private Map<String, FullPlayer> playersByName;
    private Map<FullPlayer, Timer> reincarnation;
    private List<FullPlayer> playersOnSpeed;

    public PlayerHandler(Logger logger) {
        super("[WoS][PlayerHandler]", logger);
    }

    @Override
    protected void InitVariables() {
        playersByName = new HashMap<>();
        reincarnation = new HashMap<>();
        playersOnSpeed = new ArrayList<>();
    }

    public void updateScore() {
        for (FullPlayer player : dataArray)
            player.updateScore();
    }

    @Override
    public boolean add(FullPlayer player) {
        super.add(player);
        if (playersByName.containsKey(player.getDisplayName())) return false;

        playersByName.put(player.getDisplayName(), player);
        return true;
    }

    @Override
    protected void CustomLoad(File configFile) throws IOException {
        dataArray = jsonArrayToList(configFile, FullPlayer.class);
    }

    public FullPlayer CreatePlayer(Player playerEntity) {
        FullPlayer player = new FullPlayer();

        player.setUuid(playerEntity.getUUID());
        player.setDisplayName(playerEntity.getDisplayName().getString());
        player.setAssistant(false);
        player.setBalance(WarOfSquirrels.instance.config.getConfiguration().getStartBalance());
        player.lastPosition = new Vector3(
                playerEntity.getOnPos().getX(),
                playerEntity.getOnPos().getY(),
                playerEntity.getOnPos().getZ());
        player.setLastDimension(playerEntity.getCommandSenderWorld().dimension().location().getPath());

        player.setChatTarget(BroadCastTarget.GENERAL);

        add(player);

        LogPlayerCreation(player);

        return player;
    }

    public FullPlayer CreateFakePlayer(String name) {
        FakePlayer fake = new FakePlayer();

        fake.setUuid(UUID.randomUUID());
        fake.setDisplayName(name);
        fake.setFake(true);
        fake.lastPosition = new Vector3(0, 0, 0);
        fake.setLastDimension(Level.OVERWORLD.location().getPath());

        add(fake);
        LogPlayerCreation(fake);

        return fake;
    }

    public boolean contains(String name) {
        return playersByName.containsKey(name);
    }

    @Override
    public boolean Delete(FullPlayer player) {
        super.Delete(player);
        if (player.getCity() != null)
            if (!player.getCity().removeCitizen(player, false))
                return false;

        WarOfSquirrels.instance.spreadPermissionDelete(player);

        if (reincarnation.containsKey(player)) {
            reincarnation.get(player).cancel();
        }
        reincarnation.remove(player);
        playersByName.remove(player.getDisplayName());

        return true;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Players generated : {1}", PrefixLogger, dataArray.size()));
    }

    @Override
    public void spreadPermissionDelete(IPermission target) { }

    private void LogPlayerCreation(FullPlayer player) {
        Logger.info(MessageFormat.format("{0} Player {1}({2}) created", PrefixLogger, player.getDisplayName(), player.getUuid()));
    }

    public boolean exists(UUID playerUuid) {
        return get(playerUuid) != null;
    }

    public Timer newReincarnation(FullPlayer player) {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CancelReincarnation(player);
            }
        }, WarOfSquirrels.instance.getConfig().getReincarnationTime() * 1000L);

        return timer;
    }

    public boolean isInReincarnation(FullPlayer player) {
        return reincarnation.containsKey(player);
    }

    public void SetReincarnation(FullPlayer player) {
        if (reincarnation.containsKey(player)) {
            reincarnation.get(player).cancel();
            reincarnation.remove(player);
        }
        player.setReincarnation(true);
        reincarnation.put(player, newReincarnation(player));
    }

    public void CancelReincarnation(FullPlayer player) {
        player.setReincarnation(false);
        reincarnation.remove(player);
    }

    public FullPlayer get(String displayName) {
        return playersByName.get(displayName);
    }

    public void OnTerritoryChange(FullPlayer player, Territory newTerritory) {
        if (playersOnSpeed.contains(player)) {
            player.getPlayerEntity().removeEffect(MobEffects.MOVEMENT_SPEED);
            playersOnSpeed.remove(player);
        }

        if (newTerritory.getFortification() == null || newTerritory.getFortification().getFortificationType() != IFortification.FortificationType.BASTION
                || ((Bastion) newTerritory.getFortification()).getBastionUpgrade().getRoad().getCurrentLevel() < 4) return;

        player.getPlayerEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED));
    }
}
