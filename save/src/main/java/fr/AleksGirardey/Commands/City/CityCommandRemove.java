package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.value.mutable.CollectionValue;
import org.spongepowered.api.entity.living.player.Player;
<<<<<<< HEAD
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
=======
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4

import java.util.Collection;

public class CityCommandRemove extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        String name = context.<String>getOne("[citizen]").get();
        int     id = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");

        return  (!Core.getPlayerHandler().getUuidFromName(name)
                .equals(Core.getCityHandler().<String>getElement(id, "city_playerOwner")));
    }

<<<<<<< HEAD
    public void     kick (String uuidKick, String kickName, Player sender) {
        Core.getPlayerHandler().setElement(
                uuidKick,
                "player_cityId",
                null);
        Core.getPlayerHandler().setElement(
                uuidKick,
                "player_assistant",
                false);
        Text message = Text.of(kickName + " a été kick de la ville.");
        Core.getInfoCityMap().get(Core.getPlayerHandler().<Integer>getElement(sender, "player_cityId")).getChannel().send(Text.of(TextColors.DARK_GREEN, message, TextColors.RESET));
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        String      citizen = context.<String>getOne("[citizen]").get();
        String      kick =  Core.getPlayerHandler().getUuidFromName(context.<String>getOne("[citizen]").get());

        kick(kick, citizen, player);
        if (context.hasAny("<citizen>")) {
            Collection<String>      oldCitizens = context.<String>getAll("<citizen>");
            for (String p : oldCitizens)
                kick(Core.getPlayerHandler().getUuidFromName(p), p, player);
        }
=======
    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        String citizen = context.<String>getOne("[citizen]").get();

        Core.getPlayerHandler().setElement(
                Core.getPlayerHandler().getUuidFromName(context.<String>getOne("[citizen]").get()),
                "player_cityId",
                null);
        Core.getBroadcastHandler().cityChannel(
                Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"),
                citizen  + " has been kicked from the city");
        //Core.getBroadcastHandler().kickMessage(citizen);
        if (context.hasAny("<citizen>")) {
            Collection<String>      oldCitizens = context.<String>getAll("<citizen>");

            for (String p : oldCitizens) {
                Core.getPlayerHandler().setElement(
                        Core.getPlayerHandler().getUuidFromName(p), "player_cityId", null);
                Core.getBroadcastHandler().cityChannel(
                        Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"),
                        p + " has been kicked from the city");
                //Core.getBroadcastHandler().kickMessage(citizen);
            }
        }

>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
        return CommandResult.success();
    }
}
