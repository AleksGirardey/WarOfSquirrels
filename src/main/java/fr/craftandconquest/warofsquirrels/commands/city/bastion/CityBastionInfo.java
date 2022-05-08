package fr.craftandconquest.warofsquirrels.commands.city.bastion;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.CityBastionCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.city.CityInfo;
import fr.craftandconquest.warofsquirrels.commands.extractor.IBastionExtractor;
import fr.craftandconquest.warofsquirrels.commands.extractor.ITerritoryExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CityBastionInfo extends CityBastionCommandBuilder implements ITerritoryExtractor, IBastionExtractor {
    private final static CityBastionInfo CMD_NO_ARGS = new CityBastionInfo(false);
    private final static CityBastionInfo CMD_ARGS = new CityBastionInfo(true);

    private final boolean args;

    public CityBastionInfo() {args = false; }

    public CityBastionInfo(boolean hasArgs) { args = hasArgs; }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("info")
                .executes(CMD_NO_ARGS)
                .then(getTerritoryRegister().executes(CMD_ARGS));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Territory territory;

        if (args) {
            if (!player.isAdminMode()) {
                player.sendMessage(ChatText.Error("You doesn't have permission to do that"));
                return false;
            }

            territory = getTerritory(context);
        } else {
            territory = ExtractTerritory(player);
        }

        if (territory == null || territory.getFortification() == null) {
            player.sendMessage(ChatText.Error("Cannot find suitable territory"));
            return false;
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Territory territory;

        if (args) {
            territory = getTerritory(context);
        } else {
            territory = ExtractTerritory(player);
        }

        if (territory.getFortification() != null)
            territory.getFortification().displayInfo(player);
        else
            player.sendMessage(ChatText.Error("Territory does not have any fortification."));

        return 0;
    }

    @Override public boolean suggestionIsGlobalWarTarget() { return false; }
    @Override public boolean suggestionIsFactionWarTarget() { return false; }
}
