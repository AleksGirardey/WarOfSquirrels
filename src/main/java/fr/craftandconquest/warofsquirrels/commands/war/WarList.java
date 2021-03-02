package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;

import static net.minecraft.command.Commands.literal;

public class WarList extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return literal("list").executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        WarOfSquirrels.instance.getWarHandler().DisplayList(player);
        return 1;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }
}
