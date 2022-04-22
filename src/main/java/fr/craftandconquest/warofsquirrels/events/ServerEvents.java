package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.PlayerHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.utils.BackUpUtils;
import fr.craftandconquest.warofsquirrels.utils.SpawnTeleporter;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import lombok.SneakyThrows;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = WarOfSquirrels.warOfSquirrelsModId, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {
    private final Logger logger;

    public ServerEvents(Logger logger) { this.logger = logger; }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void PlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        List<BroadCastTarget> targets = new ArrayList<>();
        Player playerEntity = event.getPlayer();

        PlayerHandler playerHandler = WarOfSquirrels.instance.getPlayerHandler();
        BroadCastHandler broadCastHandler = WarOfSquirrels.instance.getBroadCastHandler();

        FullPlayer player;

        if (!playerHandler.exists(playerEntity.getUUID(), true)) {
            player = playerHandler.CreatePlayer(playerEntity);
            SpawnTeleporter sp = new SpawnTeleporter(WarOfSquirrels.instance.getConfig().getServerSpawn());
            ServerLevel level = WarOfSquirrels.server.getLevel(WarOfSquirrels.SPAWN);
            if (level != null)
                playerEntity.changeDimension(level, sp);
        } else {
            player = playerHandler.get(playerEntity.getUUID());
            player.setChatTarget(BroadCastTarget.GENERAL);
            player.setLastDimension(event.getPlayer().getCommandSenderWorld().dimension().location().getPath());
            player.lastPosition = new Vector3(
                    (float) event.getPlayer().getBlockX(),
                    (float) event.getPlayer().getBlockY(),
                    (float) event.getPlayer().getBlockZ());
        }

        broadCastHandler.AddPlayerToWorldAnnounce(player);
        targets.add(BroadCastTarget.GENERAL);

        if (player.getCity() != null) {
            broadCastHandler.AddPlayerToTarget(player.getCity(), player);
            targets.add(player.getCity().getBroadCastTarget());

            if (WarOfSquirrels.instance.getWarHandler().Contains(player.getCity())) {
                War war = WarOfSquirrels.instance.getWarHandler().getWar(player.getCity());

                if (war.getCityDefender().equals(player.getCity()))
                    war.AddDefender(player);
            }

            if (player.getCity().getFaction() != null) {
                broadCastHandler.AddPlayerToTarget(player.getCity().getFaction(), player);
                targets.add(player.getCity().getFaction().getBroadCastTarget());
            }
        }

        logger.info(String.format("[WoS][WorldInteraction][PlayerLoggedIn] Player %s added to %s channels",
                player.getDisplayName(), targets.size()));

        // Ajouter les canaux de support et d'admin
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void PlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer().getUUID());

        if (player == null) return;

        if (WarOfSquirrels.instance.getWarHandler().Contains(player))
            WarOfSquirrels.instance.getWarHandler().getWar(player).RemovePlayer(player);

        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerToWorldAnnounce(player);
        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTargets(player);

        logger.info(String.format("[WoS][WorldInteraction][PlayerLoggedOut] Player %s removed from all channels",
                player.getDisplayName()));
    }

    @SneakyThrows
    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnServerShuttingDown(ServerStoppingEvent event) {
        WarOfSquirrels.instance.getWarHandler().CancelWars();
        WarOfSquirrels.instance.getUpdateHandler().CancelTask();
        WarOfSquirrels.instance.getUpdateHandler().SaveTask();

        BackUpUtils.DoBackUp();
    }
}
