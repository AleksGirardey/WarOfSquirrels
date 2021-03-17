package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class FactionInfoCommand extends CommandBuilder {
    private final FactionInfoCommand withArgCmd = new FactionInfoCommand(true);

    private final String argumentName = "[Faction]";

    FactionInfoCommand(boolean withArg) {
        this.withArg = withArg;
    }

    private boolean withArg;

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("info")
                .executes(this)
                .then(Commands.argument(argumentName, StringArgumentType.string())
                        .executes(withArgCmd));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        Faction faction;

        if (withArg) {
            faction = WarOfSquirrels.instance.getFactionHandler().get(context.getArgument(argumentName, String.class));
        } else {
            faction = player.getCity() != null ? player.getCity().getFaction() : null;
        }

        return faction != null;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        Faction faction;

        if (withArg) {
            faction = WarOfSquirrels.instance.getFactionHandler().get(context.getArgument(argumentName, String.class));
        } else {
            faction = player.getCity().getFaction();
        }

        player.getPlayerEntity().sendMessage(new StringTextComponent(faction.toString()));
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }
}
