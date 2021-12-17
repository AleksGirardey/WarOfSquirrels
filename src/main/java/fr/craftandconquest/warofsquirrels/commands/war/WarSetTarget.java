package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.war.War;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class WarSetTarget extends CityAssistantCommandBuilder {
    private final String playerArgumentName = "[Player]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("target")
                .then(Commands
                        .argument(playerArgumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        War war = WarOfSquirrels.instance.getWarHandler().getWar(player);
        return war.getState().equals(War.WarState.Preparation) && war.getCityDefender().equals(player.getCity());
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        WarOfSquirrels.instance.getWarHandler().getWar(player).setTarget(context.getArgument(playerArgumentName, FullPlayer.class));
        return 0;
    }
}