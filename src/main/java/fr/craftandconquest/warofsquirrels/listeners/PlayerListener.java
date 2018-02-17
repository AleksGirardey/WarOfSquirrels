package fr.craftandconquest.warofsquirrels.listeners;

import fr.craftandconquest.warofsquirrels.objects.channels.CityChannel;
import fr.craftandconquest.warofsquirrels.objects.city.InfoCity;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.utils.Utils;
import fr.craftandconquest.warofsquirrels.objects.war.PartyWar;
import fr.craftandconquest.warofsquirrels.objects.war.War;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.regex.Pattern;

public class PlayerListener {

    @Listener
    public void     onPvp(DamageEntityEvent event, @First IndirectEntityDamageSource source) {
        if (event.getTargetEntity() instanceof Player && !source.isExplosive() && source.getIndirectSource() instanceof Player) {
            Player player = (Player) source.getIndirectSource();
            Player targetPlayer = (Player) event.getTargetEntity();

            if (handleEvent(event, player.getLocation(), player) || handleEvent(event, targetPlayer.getLocation(), targetPlayer)) {
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void     onPvp(AttackEntityEvent event, @First EntityDamageSource source) {
        if (event.getTargetEntity() instanceof Player && source.getSource() instanceof Player) {
            Player player = (Player) source.getSource();
            Player targetPlayer = (Player) event.getTargetEntity();

            if (handleEvent(event, player.getLocation(), player) || handleEvent(event, targetPlayer.getLocation(), targetPlayer))
                event.setCancelled(true);
        }
    }

    private boolean handleEvent(Cancellable event, Location<World> location, Player player) {
        return false;
    }

    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death event, @First IndirectEntityDamageSource source) {
        if (event.getTargetEntity() instanceof Player) {
            event.setChannel(MessageChannel.TO_ALL);
            Core.getPlayerHandler().setReincarnation(Core.getPlayerHandler().get((Player) event.getTargetEntity()));
            if (source.getIndirectSource() instanceof Player) {
                Core.getLogger().warn("[Death] IndirectEntityDamageSource is a player");
                handleEventDeath(event, (Player) source.getIndirectSource());
            }
        }
    }

    @Listener(order = Order.FIRST)
    public void onPlayerDeath(DestructEntityEvent.Death event, @First EntityDamageSource source) {
        if (event.getTargetEntity() instanceof Player) {
            event.setChannel(MessageChannel.TO_ALL);
            Core.getPlayerHandler().setReincarnation(Core.getPlayerHandler().get((Player) event.getTargetEntity()));
            if (source.getSource() instanceof Player) {
                Core.getLogger().warn("[Death] EntityDamageSource is a player");
                handleEventDeath(event, (Player) source.getSource());
            }
        }
    }

    private void handleEventDeath(DestructEntityEvent.Death event, Player source) {
        DBPlayer victim = Core.getPlayerHandler().get((Player) event.getTargetEntity());
        DBPlayer killer = Core.getPlayerHandler().get(source);

        if (Core.getWarHandler().Contains(killer)
                && Core.getWarHandler().Contains(victim))
            Core.getWarHandler().AddPoints(killer, victim);
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
            transform = new Transform<>(Utils.getNearestSpawn(player));
            event.setToTransform(transform);
        }
    }

    @Listener(order = Order.FIRST)
    public void onPlayerLogin(ClientConnectionEvent.Join event) {
        DBPlayer player = Core.getPlayerHandler().get(event.getTargetEntity());

        event.setChannel(MessageChannel.TO_ALL);

        if (player == null)
            Core.getPlayerHandler().add(event.getTargetEntity());

        player = Core.getPlayerHandler().get(event.getTargetEntity());

        if (player.getCity() != null) {
            InfoCity ic = Core.getInfoCityMap().get(player.getCity());
            if (ic.getChannel() == null)
                ic.setChannel(new CityChannel(player.getCity()));
            if (!ic.getChannel().getMembers().contains(player.getUser().getPlayer().get())) {
                ic.getChannel().addMember(player.getUser().getPlayer().get());
                Core.getLogger().info("Player '" + player.getDisplayName() + "' added to city channel (" + player.getCity().getDisplayName() + ")");
            }
        }
        Core.getBroadcastHandler().getGlobalChannel().addMember(player.getUser().getPlayer().get());
        player.getUser().getPlayer().get().setMessageChannel(Core.getBroadcastHandler().getGlobalChannel());
    }

    @Listener(order = Order.FIRST)
    public void onPlayerLogout(ClientConnectionEvent.Disconnect event) {
        MutableMessageChannel   channel;
        DBPlayer        player = Core.getPlayerHandler().get(event.getTargetEntity());
        PartyWar        partyWar;

        if (Core.getPartyHandler().contains(player)) {
            partyWar = Core.getPartyHandler().getFromPlayer(player);
            if (partyWar.getLeader() == player)
                Core.getPartyHandler().removeParty(player);
            else
                partyWar.remove(player);
        }
        if (Core.getWarHandler().Contains(player)) {
            War war = Core.getWarHandler().getWar(player);
            war.removePlayer(player);
            if (war.isTarget(player)) {
                if (war.getPhase().equals(War.WarState.War.toString()))
                    war.addAttackerPointsTarget();
                else
                    war.setTarget();
            }
        }
        if (player.getCity() != null) {
            channel = Core.getInfoCityMap().get(player.getCity()).getChannel();
            Core.getLogger().info("[Logout] Channel '" + channel.toString() + "' contains '" + channel.getMembers().toString() + "'");
        }
        if (Core.getBroadcastHandler().getGlobalChannel().getMembers().contains(event.getTargetEntity()))
            Core.getBroadcastHandler().getGlobalChannel().removeMember(event.getTargetEntity());

        // INSERT ZOMBIE PIGMAN STUFF

        event.setChannel(MessageChannel.TO_ALL);
    }
}
