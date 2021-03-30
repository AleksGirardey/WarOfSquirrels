package fr.craftandconquest.warofsquirrels.commands.faction.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.extractor.IFactionExtractor;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPermissionExtractor;
import fr.craftandconquest.warofsquirrels.commands.faction.FactionCommandAssistant;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;

public class FactionSetAlly extends FactionCommandAssistant implements IFactionExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("ally").then(getArgumentRegister().executes(this));
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
