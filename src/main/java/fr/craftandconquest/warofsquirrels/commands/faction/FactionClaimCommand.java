package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.extractor.ITerritoryExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class FactionClaimCommand extends FactionCommandAssistant implements ITerritoryExtractor {
    private final String argumentName = "[TerritoryName]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("claim")
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Territory territory = ExtractTerritory(player);
        Influence influence = WarOfSquirrels.instance.getInfluenceHandler().get(player.getCity().getFaction(), territory);

        return influence != null && influence.getValue() >= WarOfSquirrels.instance.getConfig().getTerritoryClaimLimit();
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Territory territory = ExtractTerritory(player);

        territory.SetFaction(player.getCity().getFaction());
        String name = context.getArgument(argumentName, String.class);
        territory.setName(name);

        int posX = territory.getPosX();
        int posZ = territory.getPosZ();
        int territorySize = WarOfSquirrels.instance.getConfig().getTerritorySize();

        MutableComponent message = ChatText.Colored("La faction '" + territory.getFaction().getDisplayName() + "'"
                + " a revendiqué l'appartenance du territoire maintenant appellé '" + territory.getName() + "' situé en ["
                + posX + ";" + posZ + "](~" + posX * territorySize + ";" + "~" + posZ * territorySize + ")", ChatFormatting.GOLD);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
        WarOfSquirrels.instance.getInfluenceHandler().ResetOthersInfluence(territory);
        WarOfSquirrels.instance.getTerritoryHandler().Save();
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
