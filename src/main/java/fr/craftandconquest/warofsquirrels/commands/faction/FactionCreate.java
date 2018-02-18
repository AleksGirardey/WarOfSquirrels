package fr.craftandconquest.warofsquirrels.commands.faction;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.handlers.CityHandler;
import fr.craftandconquest.warofsquirrels.handlers.FactionHandler;
import fr.craftandconquest.warofsquirrels.objects.channels.CityChannel;
import fr.craftandconquest.warofsquirrels.objects.channels.FactionChannel;
import fr.craftandconquest.warofsquirrels.objects.city.InfoCity;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Chunk;
import fr.craftandconquest.warofsquirrels.objects.dbobject.City;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Faction;
import fr.craftandconquest.warofsquirrels.objects.faction.InfoFaction;
import fr.craftandconquest.warofsquirrels.objects.utils.Utils;
import fr.craftandconquest.warofsquirrels.objects.war.PartyWar;
import fr.craftandconquest.warofsquirrels.handlers.CityHandler;
import fr.craftandconquest.warofsquirrels.handlers.FactionHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class FactionCreate extends Commands {
    @Override
    protected boolean       CanDoIt(DBPlayer player) {
        if (super.CanDoIt(player) && Core.getPartyHandler().isLeader(player)) {
            PartyWar    party = Core.getPartyHandler().getPartyFromLeader(player);

            if (player.hasAdminMode())
                return true;
            else if (party.size() >= 4
                    && party.createCityCheck())
                return true;
            player.sendMessage(Text.of(TextColors.RED, "Votre groupe doit contenir au moins 3 vagabons."));
            return false;
        }
        player.sendMessage(Text.of(TextColors.RED, "Vous devez créer un groupe en premier.", TextColors.RESET));
        return false;
    }

    @Override
    protected boolean   SpecialCheck(DBPlayer player, CommandContext context) {
        String          factionName = context.<String>getOne("[faction_name]").orElse(""),
                cityName = context.<String>getOne("[capital_name]").orElse("");
        Text            message;
        int             x, z;

        x = player.getLastChunkX();
        z = player.getLastChunkZ();

        if (player.getCity() == null) {
            if (Utils.CanPlaceCity(x, z)) {
                if (Utils.checkFactionName(factionName) && Utils.checkCityName(cityName)) {
                    if (cityName.length() >= 3 && factionName.length() >= 3)
                        return true;
                    message = Text.of("3 caractères minimum.");
                } else
                    message = Text.of("Le nom de votre faction ou de votre capitale est incorrect.");
            } else
                message = Text.of("Vous ne pouvez pas vous installer ici, trop proche d'une autre civilisation.");
        } else
            message = Text.of("Vous devez quitter votre ville avant.");

        player.sendMessage(Text.of(TextColors.RED, message, TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        String                  factionName = context.<String>getOne("[faction_name]").orElse(""),
                                capitalName = context.<String>getOne("[capital_name]").orElse("");
        CityHandler cih = Core.getCityHandler();
        FactionHandler fh = Core.getFactionHandler();
        Faction                 faction = fh.add(null, factionName);
        City                    city = cih.add(player, capitalName, faction);
        Chunk                   chunk;
        //Territory             territory;
        PartyWar                party;

        faction.setCapital(city);
        player.setCity(city);
        chunk = new Chunk(player, true, false);
        Core.getChunkHandler().add(chunk);
        Core.getInfoCityMap().put(city, new InfoCity(city));
        Core.getInfoCityMap().get(city).setChannel(new CityChannel(city));

        Core.getInfoFactionMap().put(faction, new InfoFaction(faction));
        Core.getInfoFactionMap().get(faction).setChannel(new FactionChannel(faction));

        party = Core.getPartyHandler().getPartyFromLeader(player);
        for (DBPlayer p : party.toList()) {
            p.setCity(city);
            city.addCitizen(p);
            Core.getInfoCityMap().get(city).getChannel().addMember(p.getUser().getPlayer().get());
            Core.getInfoFactionMap().get(faction).getChannel().addMember(p.getUser().getPlayer().get());
        }
        faction.addCity(city);
        Core.getDiplomacyHandler().init(faction);

        Text        message = Text.of(player.getDisplayName() + " est maintenant le leader de la faction ", TextStyles.BOLD, faction.getDisplayName(), TextStyles.RESET, " dont la capitale est ",
                TextStyles.BOLD, city.getDisplayName(), TextStyles.RESET, ".");

        Core.SendText(Text.of(TextColors.GOLD, message, TextColors.RESET));
        return CommandResult.success();
    }
}
