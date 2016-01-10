package fr.AleksGirardey.Commands.CityCommand;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;

public abstract class CityCommandAssistant extends CityCommandMayor {

    @Override
    protected boolean CanDoIt(Player player) {
        return super.CanDoIt(player) && Core.getPlayerHandler().isAssistant(player);
    }
}
