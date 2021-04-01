package fr.craftandconquest.warofsquirrels.commands.party;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class PartyHelpCommand extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("help").executes(this);
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        player.getPlayerEntity().sendMessage(new StringTextComponent(
                "--==| party help |==--\n" +
                        "/party info\n" +
                        "/party create\n" +
                        "/party delete\n" +
                        "/party invite [PlayerName]\n" +
                        "/party remove [PlayerName]\n" +
                        "/party leave\n").applyTextStyle(TextFormatting.GREEN));
        return 0;
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) { return true; }

    @Override
    protected ITextComponent ErrorMessage() { return null; }
}
