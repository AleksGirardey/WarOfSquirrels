package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;

public class PlayerHandler extends Handler<FullPlayer> {

    private static final String DirName = "/WorldData";
    private static final String JsonName = "/PlayerHandler.json";

    private final Map<String, FullPlayer> playersByName;

    private final Map<FullPlayer, Timer> reincarnation;

    public PlayerHandler(Logger logger) {
        super("[WoS][PlayerHandler]", logger);
        playersByName = new HashMap<>();
        reincarnation = new HashMap<>();

        if (!Init()) return;
        if (!Load(new TypeReference<List<FullPlayer>>() {
        })) return;

        Log();
    }

    public void updateDependencies() {
        for (FullPlayer player : dataArray)
            player.updateDependencies();
    }

    @Override
    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(this::add);
        return true;
    }

    public boolean add(FullPlayer player) {
        if (!dataArray.contains(player)) {
            playersByName.put(player.getDisplayName(), player);
        }
        return true;
    }

    public FullPlayer CreatePlayer(Player playerEntity) {
        FullPlayer player = new FullPlayer();

        player.setUuid(playerEntity.getUUID());
        player.setDisplayName(playerEntity.getDisplayName().getString());
        player.setAssistant(false);
        player.setBalance(WarOfSquirrels.instance.config.getConfiguration().getStartBalance());
        player.lastPosition = new Vector3(
                (int) playerEntity.getOnPos().getX(),
                (int) playerEntity.getOnPos().getY(),
                (int) playerEntity.getOnPos().getZ());
        player.lastDimension = playerEntity.getCommandSenderWorld().dimension();

        playersByName.put(player.getDisplayName(), player);
        dataArray.add(player);

        LogPlayerCreation(player);

        return player;
    }

    @Override
    public boolean Delete(FullPlayer player) {
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
        Logger.info(MessageFormat.format("{0} Players generated : {1}",
                PrefixLogger, dataArray.size()));
    }

    @Override
    public String getConfigDir() {
        return WarOfSquirrels.warOfSquirrelsConfigDir + DirName;
    }

    @Override
    protected String getConfigPath() {
        return WarOfSquirrels.warOfSquirrelsConfigDir + DirName + JsonName;
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {
        // Nothing to do;
    }

    private void LogPlayerCreation(FullPlayer player) {
        Logger.info(MessageFormat.format("{0} Player {1}({2}) created",
                PrefixLogger, player.getDisplayName(), player.getUuid()));
    }

    public boolean exists(UUID playerUuid) {
        return exists(playerUuid, false);
    }

    public boolean exists(UUID playerUuid, boolean log) {
        return get(playerUuid, log) != null;
    }

    public Timer newReincarnation(FullPlayer player) {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CancelReincarnation(player);
            }
        }, WarOfSquirrels.instance.getConfig().getReincarnationTime() * 1000);

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

    public FullPlayer get(UUID uuid) {
        return get(uuid, false);
    }

    public FullPlayer get(UUID uuid, boolean log) {
        for (FullPlayer player : dataArray) {
            if (player.getUuid().equals(uuid))
                return player;
        }

        return null;
    }

    public FullPlayer get(String displayName) {
        return playersByName.get(displayName);
    }
}
