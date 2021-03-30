package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.ITerritoryExtractor;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.channels.FactionChannel;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class FactionCreateCommand extends CityMayorCommandBuilder implements ITerritoryExtractor {
    private final String factionName = "[FactionName]";
    private final String territoryName = "[TerritoryName]";

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("create")
                .then(Commands
                        .argument(factionName, StringArgumentType.string())
                        .then(Commands.argument(territoryName, StringArgumentType.string())
                                .executes(this)));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        // Une faction ne peut être créé que si :
        //  - La ville possède assez d'influence sur le territoire
        //  - La ville n'a pas déjà une faction

        Territory territory = ExtractTerritory(player);
        Influence influence = WarOfSquirrels.instance.getInfluenceHandler().get(player.getCity(), territory);

        if (player.getCity().getFaction() == null
                && influence.getValue() >= WarOfSquirrels.instance.getConfig().getBaseInfluenceRequired()) return true;

        StringTextComponent message = new StringTextComponent("Vous ne remplissez pas les conditions nécessaire pour former une faction.");
        message.applyTextStyle(TextFormatting.RED);
        player.getPlayerEntity().sendMessage(message);
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        String name = context.getArgument(factionName, String.class);
        Territory territory = ExtractTerritory(player);
        Faction faction = WarOfSquirrels.instance.getFactionHandler().CreateFaction(name, player.getCity());

        player.getCity().SetFaction(faction);
        territory.SetFaction(faction);
        territory.setName(context.getArgument(territoryName, String.class));

        StringTextComponent message = new StringTextComponent(player.getDisplayName() + " forme la faction '"
                + name + "' dont la capitale est '" + player.getCity().displayName + "'");

        message.applyTextStyle(TextFormatting.BOLD).applyTextStyle(TextFormatting.GOLD);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
        WarOfSquirrels.instance.getBroadCastHandler().AddTarget(faction, new FactionChannel(faction));

        for (Player p : player.getCity().getCitizens())
            WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(faction, p);

        WarOfSquirrels.instance.getInfluenceHandler().ResetOthersInfluence(territory);
        WarOfSquirrels.instance.getCityHandler().Save();
        WarOfSquirrels.instance.getTerritoryHandler().Save();
        return 0;
    }
}
