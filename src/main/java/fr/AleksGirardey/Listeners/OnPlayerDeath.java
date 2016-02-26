package fr.AleksGirardey.Listeners;


import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;

public class OnPlayerDeath {

    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death event) {
        if (event.getCause().containsType(Player.class)) {
            EntityDamageSource      k = event.getCause().first(EntityDamageSource.class).orElse(null);
            Player                  victim = (Player) event.getTargetEntity(), killer;
            PlayerHandler           ph = Core.getPlayerHandler();
            int                     killerCity, victimCity;

            if (k.getSource() instanceof Player) {
                Core.Send(ph.<String>getElement((Player) k.getSource(), "player_displayName") + " / " + ph.<String>getElement(victim, "player_displayName"));
                killer = (Player) k.getSource();
                if (Core.getWarHandler().Contains(killer) && Core.getWarHandler().Contains(victim)) {
                    Core.getWarHandler().AddPoints(killer, victim);
                }
                /**
                 ** Add personnal points
                 **/
            }
        }
    }
}
