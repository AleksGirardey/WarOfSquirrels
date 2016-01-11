package fr.AleksGirardey.Commands;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;

public abstract class CityCommandAssistant extends CityCommandMayor {

    @Override
    protected boolean CanDoIt(Player player) {
        return super.CanDoIt(player) || Core.getPlayerHandler().<Boolean>getElement(
                player,
                "player_assistant");
    }
}
