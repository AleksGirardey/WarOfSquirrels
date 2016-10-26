package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
<<<<<<< HEAD
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
=======
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4

public abstract class CityCommandAssistant extends CityCommandMayor {

    @Override
    protected boolean CanDoIt(Player player) {
<<<<<<< HEAD
        if (super.CanDoIt(player) || Core.getPlayerHandler().<Boolean>getElement(
                player,
                "player_assistant"))
            return true;
        else {
            player.sendMessage(Text.of(TextColors.RED, "Vous devez être au minimum assistant pour utiliser cette commande.", TextColors.RESET));
            return false;
        }
=======
        return super.CanDoIt(player) || Core.getPlayerHandler().<Boolean>getElement(
                player,
                "player_assistant");
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
    }
}
