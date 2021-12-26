package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;

public class FactionHelpCommand extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return null;
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        player.sendMessage(ChatText.Success("--==| faction help |==--\n" +
                "/faction info <faction>\n" +
                "/faction list\n" +
                "/faction create [FactionName] [TerritoryName]\n" +
                "/faction delete\n" +
                "/faction set ..."));
        return 0;
    }
}
