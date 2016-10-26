package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public abstract class CityCommandAssistant extends CityCommandMayor {

    @Override
    protected boolean CanDoIt(Player player) {
        if (super.CanDoIt(player) || Core.getPlayerHandler().<Boolean>getElement(
                player,
                "player_assistant"))
            return true;
        else {
            player.sendMessage(Text.of(TextColors.RED, "Vous devez être au minimum assistant pour utiliser cette commande.", TextColors.RESET));
            return false;
        }
    }
}
