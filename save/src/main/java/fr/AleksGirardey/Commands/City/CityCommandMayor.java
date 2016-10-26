package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;

public abstract class CityCommandMayor extends CityCommand {
    @Override
    protected boolean CanDoIt(Player player) {
        return super.CanDoIt(player) && Core.getPlayerHandler().isOwner(player);
    }
}
