package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.ITerritoryExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.channels.FactionChannel;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class FactionCreateCommand extends CityMayorCommandBuilder implements ITerritoryExtractor {
    private final String factionName = "[FactionName]";
    private final String territoryName = "[TerritoryName]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("create")
                .then(Commands
                        .argument(factionName, StringArgumentType.string())
                        .then(Commands.argument(territoryName, StringArgumentType.string())
                                .executes(this)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        // Une faction ne peut être créé que si :
        //  - La ville possède assez d'influence sur le territoire
        //  - La ville n'a pas déjà une faction

        Territory territory = ExtractTerritory(player);
        Influence influence = WarOfSquirrels.instance.getInfluenceHandler().get(player.getCity(), territory);

        if (player.getCity().getFaction() == null
                && influence.getValue() >= WarOfSquirrels.instance.getConfig().getBaseInfluenceRequired()) return true;

        player.sendMessage(ChatText.Error("You do not met requirements to create a faction."));
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String fName = context.getArgument(factionName, String.class);
        String tName = context.getArgument(territoryName, String.class);
        Territory territory = ExtractTerritory(player);
        Faction faction = WarOfSquirrels.instance.getFactionHandler().CreateFaction(fName, player.getCity());

        player.getCity().SetFaction(faction);

        if (!WarOfSquirrels.instance.getTerritoryHandler().Claim(territory.getPosX(), territory.getPosZ(), faction, player.getCity(), tName))
            return -1;

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(ChatText.Colored(
                player.getDisplayName() + " has formed the nation '" + fName + "' and set '" + player.getCity().displayName + "' as capital", ChatFormatting.GOLD));
        WarOfSquirrels.instance.getBroadCastHandler().AddTarget(faction, new FactionChannel(faction));

        for (FullPlayer p : player.getCity().getCitizens())
            WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(faction, p);

        WarOfSquirrels.instance.getInfluenceHandler().ResetOthersInfluence(territory);
        WarOfSquirrels.instance.getCityHandler().Save();
        WarOfSquirrels.instance.getTerritoryHandler().Save();
        return 0;
    }
}
