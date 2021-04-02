package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.extractor.ITerritoryExtractor;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class FactionClaimCommand extends FactionCommandAssistant implements ITerritoryExtractor {
    private final String argumentName = "[TerritoryName]";

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands
                .literal("claim")
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        Territory territory = ExtractTerritory(player);
        Influence influence = WarOfSquirrels.instance.getInfluenceHandler().get(player.getCity().getFaction(), territory);

        return influence != null && influence.getValue() >= WarOfSquirrels.instance.getConfig().getTerritoryClaimLimit();
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        Territory territory = ExtractTerritory(player);

        territory.SetFaction(player.getCity().getFaction());
        String name = context.getArgument(argumentName, String.class);
        territory.setName(name);

        int posX = territory.getPosX();
        int posZ = territory.getPosZ();
        int territorySize = WarOfSquirrels.instance.getConfig().getTerritorySize();

        StringTextComponent message = new StringTextComponent("La faction '" + territory.getFaction().getDisplayName() + "'"
                + " a revendiqué l'appartenance du territoire maintenant appellé '" + territory.getName() + "' situé en ["
                + posX + ";" + posZ + "](~" + posX * territorySize + ";" + "~" + posZ * territorySize + ")");

        message.applyTextStyle(TextFormatting.GOLD);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
        WarOfSquirrels.instance.getInfluenceHandler().ResetOthersInfluence(territory);
        WarOfSquirrels.instance.getTerritoryHandler().Save();
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() { return null; }
}
