package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class CityHelp extends CommandBuilder {
    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Player entity = player.getPlayerEntity();

        MutableComponent message = ChatText.Colored(
                """
                        --==| city help |==--
                        /city create [name]
                        /city info <name>
                        /city claim
                        /city unclaim
                        /city set ...
                        /city add [player]
                        /city remove [citizen]
                        /city leave
                        /city list
                        /city cubo""", ChatFormatting.WHITE);

        entity.sendMessage(message, Util.NIL_UUID);

        return 0;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("help").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }


    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
