package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.value.mutable.CollectionValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.List;

public class            CityCommandRemove extends CityCommandAssistant {
    @Override
    protected boolean   SpecialCheck(DBPlayer player, CommandContext context) {
        DBPlayer          target = context.<DBPlayer>getOne("[citizen]").get();
        Collection<DBPlayer> targets = context.<DBPlayer>getAll("<citizen>");

        if (target == player.getCity().getOwner() ||
                target == player ||
                targets.contains(player.getCity().getOwner()) ||
                targets.contains(player)) {
            player.sendMessage(Text.of("Vous ne pouvez pas vous expulser de la ville, ni expulser le maire."));
            return false;
        }
        return true;
    }

    private void         kick(DBPlayer kicked, DBPlayer sender) {
        sender.getCity().removeCitizen(kicked);
        kicked.setCity(null);
        if (kicked.isAssistant())
            kicked.setAssistant(false);
        Text message = Text.of(kicked.getDisplayName() + " a été kick de la ville.");
        kicked.sendMessage(Text.of(TextColors.RED, "Vous avez été kick de votre ville.", TextColors.RESET));
        Core.getInfoCityMap().get(sender.getCity()).getChannel().send(Text.of(TextColors.DARK_GREEN, message, TextColors.RESET));
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        DBPlayer    citizen = context.<DBPlayer>getOne("[citizen]").orElse(null);

        this.kick(citizen, player);
        if (context.hasAny("<citizen>")) {
            Collection<DBPlayer>      oldCitizens = context.<DBPlayer>getAll("<citizen>");
            for (DBPlayer kicked : oldCitizens) {
                if (kicked != player.getCity().getOwner())
                    kick(kicked, player);
            }
        }
        return CommandResult.success();
    }
}
