package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Handlers.ChunkHandler;
import fr.AleksGirardey.Handlers.CityHandler;
import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.*;
import fr.AleksGirardey.Objects.Channels.CityChannel;
import fr.AleksGirardey.Objects.City.InfoCity;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Utilitaires.ConfigLoader;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class            CityCommandCreate extends Commands {
    @Override
    protected boolean   CanDoIt(DBPlayer player) {
        if (super.CanDoIt(player) && Core.getPartyHandler().isLeader(player)) {
            if (player.getUser().getPlayer().get().hasPermission("minecraft.command.op"))
                return true;
            else if (Core.getPartyHandler().getPartyFromLeader(player).size() >= 4
                    && Core.getPartyHandler().getPartyFromLeader(player).createCityCheck())
                return true;
            player.sendMessage(Text.of(TextColors.RED, "Your party must contain 3 wanderers in order to create a city", TextColors.RESET));
            return false;
        }
        player.sendMessage(Text.of(TextColors.RED, "You need to create a party to create a city", TextColors.RESET));
        return false;
    }

    @Override
    protected boolean       SpecialCheck(DBPlayer player, CommandContext context) {
        String              name = context.<String>getOne("City name").get();
        Text                message;
        int                 x, z;

        x = player.getUser().getPlayer().get().getLocation().getBlockX() / 16;
        z = player.getUser().getPlayer().get().getLocation().getBlockZ() / 16;

        if (player.getCity() == null) {
            if (!Core.getChunkHandler().exists(x, z) && Utils.NearestHomeblock(x, z) >= ConfigLoader.distanceCities) {
                if (Utils.checkCityName(name)) {
                    if (name.length() >= 3)
                        return true;
                    message = Text.of("City name must have at least 3 characters");
                } else
                    message = Text.of("City name contains wrong characters or is already used.");
            } else
                message = Text.of("You can't set a new city here ! Too close from civilization");
        } else
            message = Text.of("Leave your city first !");
        player.sendMessage(Text.of(TextColors.RED, message, TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult         ExecCommand(DBPlayer player, CommandContext context) {
        String                      cityName = context.<String>getOne("City name").get();
        CityHandler                 cih = Core.getCityHandler();
        ChunkHandler                chh = Core.getChunkHandler();
        City                        city = cih.add(player, cityName);
        Chunk                       chunk;

        player.setCity(city);
        chunk = new Chunk(player, true, false);
        chh.add(chunk);
        Core.getInfoCityMap().put(city, new InfoCity(city));
        Core.getInfoCityMap().get(city).setChannel(new CityChannel(city));

        for (DBPlayer p : Core.getPartyHandler().getPartyFromLeader(player).toList()) {
            p.setCity(city);
            city.addCitizen(p);
            Core.getInfoCityMap().get(city).getChannel().addMember(p.getUser().getPlayer().get());
        }
        Text message = Text.of("[BREAKING NEWS] " + cityName + " have been created by " + player.getDisplayName());
        Core.SendText(Text.of(TextColors.GOLD, message, TextColors.RESET));
        return CommandResult.success();
    }
}
