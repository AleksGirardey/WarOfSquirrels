package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Channels.CityChannel;
import fr.AleksGirardey.Objects.City.InfoCity;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.world.World;

import java.sql.SQLException;

public class PlayerListener {

    @Listener(order = Order.FIRST)
    public void onPlayerDeath(DestructEntityEvent.Death event) {
        EntityDamageSource source = event.getCause().first(EntityDamageSource.class).orElse(null);
        Entity check;

        if (source == null)
            return;

        if (event.getTargetEntity() instanceof Player) {
            event.setChannel(MessageChannel.TO_ALL);
            Core.getPlayerHandler().setReincarnation(Core.getPlayerHandler().get((Player) event.getTargetEntity()));
        }

        check = source.getSource();
        if (check instanceof Player && event.getTargetEntity() instanceof Player) {
            DBPlayer victim = Core.getPlayerHandler().get((Player) event.getTargetEntity()),
                    killer = Core.getPlayerHandler().get((Player) check);

            if (Core.getWarHandler().Contains(killer) && Core.getWarHandler().Contains(victim))
                Core.getWarHandler().AddPoints(killer, victim);
            /*

              Add personnal points && money transfer

            */
        }
    }

    @Listener(order = Order.FIRST)
    public void onPlayerDamaged(DamageEntityEvent event) {
        if (event.getTargetEntity() instanceof Player) {
            DBPlayer player = Core.getPlayerHandler().get((Player) event.getTargetEntity());

            if (player.isInReincarnation())
                event.setBaseDamage(100);
        }
    }

    @Listener(order = Order.FIRST)
    public void onPlayerListener(RespawnPlayerEvent event) {
        DBPlayer player = Core.getPlayerHandler().get(event.getTargetEntity());
        Transform<World> transform;

        if (player.getCity() != null) {
            transform = new Transform<World>(Utils.getNearestSpawn(player));
            event.setToTransform(transform);
        }
    }

    @Listener(order = Order.FIRST)
    public void onPlayerLogin(ClientConnectionEvent.Join event) throws SQLException {
        DBPlayer player = Core.getPlayerHandler().get(event.getTargetEntity());

        event.setChannel(MessageChannel.TO_ALL);

        if (player == null)
            Core.getPlayerHandler().add(event.getTargetEntity());

        player = Core.getPlayerHandler().get(event.getTargetEntity());

        if (player.getCity() != null) {
            InfoCity ic = Core.getInfoCityMap().get(player.getCity());
            if (ic.getChannel() == null)
                ic.setChannel(new CityChannel(player.getCity()));
            ic.getChannel().addMember(player.getUser().getPlayer().get());
            Core.getLogger().info("Player '" + player.getDisplayName() + "' added to city channel (" + player.getCity().getDisplayName() + ")");
        }
        Core.getBroadcastHandler().getGlobalChannel().addMember(player.getUser().getPlayer().get());
        player.getUser().getPlayer().get().setMessageChannel(Core.getBroadcastHandler().getGlobalChannel());
    }

    @Listener(order = Order.FIRST)
    public void onPlayerLogout(ClientConnectionEvent.Disconnect event) {
        event.setChannel(MessageChannel.TO_ALL);
    }
}
