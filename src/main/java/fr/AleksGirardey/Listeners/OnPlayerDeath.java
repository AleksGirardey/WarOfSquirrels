package fr.AleksGirardey.Listeners;


import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;

public class OnPlayerDeath {

    @Listener
    public void                 onPlayerDeath(DestructEntityEvent.Death event) {
        EntityDamageSource      source = event.getCause().first(EntityDamageSource.class).orElse(null);
        Entity                  check = null;
        if (source == null)
            return;

        check = source.getSource();
        if (check instanceof Player && event.getTargetEntity() instanceof Player) {
            DBPlayer                victim = Core.getPlayerHandler().get((Player) event.getTargetEntity()),
                killer = Core.getPlayerHandler().get((Player) check);
            PlayerHandler           ph = Core.getPlayerHandler();

            Core.Send(killer.getDisplayName() + " / " + victim.getDisplayName());
            if (Core.getWarHandler().Contains(killer) && Core.getWarHandler().Contains(victim))
                Core.getWarHandler().AddPoints(killer, victim);
            /*
              Add personnal points
             */
        }
    }
}