package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.war.War;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class WarSetTarget extends CityAssistantCommandBuilder {
    private final String playerArgumentName = "[Player]";

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands
                .literal("target")
                .then(Commands
                        .argument(playerArgumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        War war = WarOfSquirrels.instance.getWarHandler().getWar(player);
        return war.getState().equals(War.WarState.Preparation) && war.getCityDefender().equals(player.getCity());
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        WarOfSquirrels.instance.getWarHandler().getWar(player).setTarget(context.getArgument(playerArgumentName, Player.class));
        return 0;
    }
}