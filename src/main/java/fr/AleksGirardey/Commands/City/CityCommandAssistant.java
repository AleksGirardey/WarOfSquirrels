package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public abstract class CityCommandAssistant extends CityCommandMayor {

    @Override
    protected boolean CanDoIt(DBPlayer player) {
        if (super.CanDoIt(player) || player.isAssistant())
            return true;
        else {
            player.sendMessage(Text.of(TextColors.RED, "Vous devez être au minimum assistant pour utiliser cette commande.", TextColors.RESET));
            return false;
        }
    }

    protected abstract boolean          SpecialCheck(DBPlayer player, CommandContext context);
}
