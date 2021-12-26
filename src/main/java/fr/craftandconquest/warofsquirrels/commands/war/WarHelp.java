package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class WarHelp extends CommandBuilder {
    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        MutableComponent message = ChatText.Colored("""
                ---=== /war ===---
                - war attack
                - war info
                - war join
                - war leave
                - war list
                - war target""", ChatFormatting.BLUE);

        player.sendMessage(message);

        return 0;
    }

    /**
     * No Implementations needed
     *
     * @return
     **/

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return null;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }
}
