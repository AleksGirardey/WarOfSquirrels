package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;

public class PlayerHandler extends Handler<Player> {

    private static final String DirName = "/WorldData";
    private static final String JsonName = "/PlayerHandler.json";

    private final Map<String, Player> playersByName;
    private final Map<PlayerEntity, Player> playersByEntity;

    private final Map<Player, Timer> reincarnation;

    public PlayerHandler(Logger logger) {
        super("[WoS][PlayerHandler]", logger);
        playersByName = new HashMap<>();
        playersByEntity = new HashMap<>();
        reincarnation = new HashMap<>();

        if (!Init()) return;
        if (!Load(new TypeReference<List<Player>>() {})) return;

        Log();
    }

    public void updateDependencies() {
        for (Player player : dataArray)
            player.updateDependencies();
    }

    @Override
    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(this::add);
        return true;
    }

    public boolean add(Player player) {
        if (!dataArray.contains(player)) {
            playersByName.put(player.getDisplayName(), player);
            playersByEntity.put(player.getPlayerEntity(), player);
        }
        return true;
    }

    public Player CreatePlayer(PlayerEntity playerEntity) {
        Player player = new Player();

        player.setPlayerEntity(playerEntity);
        player.setUuid(playerEntity.getUniqueID());
        player.setDisplayName(playerEntity.getDisplayName().getString());
        player.setAssistant(false);
        player.setBalance(WarOfSquirrels.instance.config.getConfiguration().getStartBalance());
        player.lastPosition = playerEntity.getPositionVec();
        player.lastDimension = playerEntity.dimension;

        if (!add(player)) return null;

        Save(playersByName.values());
        LogPlayerCreation(player);

        return player;
    }

    @Override
    public boolean Delete(Player player) {
        if (player.getCity() != null)
            if (!player.getCity().removeCitizen(player))
                return false;

        WarOfSquirrels.instance.spreadPermissionDelete(player);

        if (reincarnation.containsKey(player)) {
            reincarnation.get(player).cancel();
        }
        reincarnation.remove(player);
        playersByName.remove(player.getDisplayName());
        playersByEntity.remove(player.getPlayerEntity());

        Save(playersByName.values());
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

    private void LogPlayerCreation(Player player) {
        Logger.info(MessageFormat.format("{0} Player '{1}'({2}) created",
                PrefixLogger, player.getDisplayName(), player.getUuid()));
    }

    public Player get(PlayerEntity playerEntity) {
        return playersByEntity.get(playerEntity);
    }

    public boolean exists(PlayerEntity player) { return playersByEntity.containsKey(player); }

    public Timer newReincarnation(Player player) {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CancelReincarnation(player);
            }
        }, WarOfSquirrels.instance.getConfig().getReincarnationTime() * 1000);

        return timer;
    }

    public void SetReincarnation(Player player) {
        if (reincarnation.containsKey(player)) {
            reincarnation.get(player).cancel();
            reincarnation.remove(player);
        }
        player.setReincarnation(true);
        reincarnation.put(player, newReincarnation(player));
    }

    public void CancelReincarnation(Player player) {
        player.setReincarnation(false);
        reincarnation.remove(player);
    }

    public Player get(UUID uuid) {
        for (Player player : dataArray) {
            if (player.getUuid() == uuid)
                return player;
        }

        return null;
    }

    public Player get(String displayName) {
        return playersByName.get(displayName);
    }
}
