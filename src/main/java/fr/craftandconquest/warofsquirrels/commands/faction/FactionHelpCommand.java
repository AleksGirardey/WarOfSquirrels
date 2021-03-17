package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class FactionHelpCommand extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() { return null; }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) { return true; }

    @Override
    protected ITextComponent ErrorMessage() { return null; }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        player.getPlayerEntity().sendMessage(new StringTextComponent("--==| faction help |==--\n" +
                "/faction info <faction>\n" +
                "/faction list\n" +
                "/faction create [name] [cityName]\n" +
                "/faction delete\n" +
                "/faction set ...").applyTextStyle(TextFormatting.GREEN));
        return 0;
    }
}
