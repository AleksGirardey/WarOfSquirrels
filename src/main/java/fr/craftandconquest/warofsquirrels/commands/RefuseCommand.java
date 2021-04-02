package fr.craftandconquest.warofsquirrels.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;

public class RefuseCommand extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("refuse").executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        return WarOfSquirrels.instance.getInvitationHandler().HandleInvitation(player, false) ? 1 : 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }
}
