package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;

public class FactionDeleteCommand extends FactionCommandMayor implements IAdminCommand {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return null;
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }
}
