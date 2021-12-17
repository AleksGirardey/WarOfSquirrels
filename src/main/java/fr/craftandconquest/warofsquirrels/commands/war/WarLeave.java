package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class WarLeave extends CityCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("leave").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        War war = WarOfSquirrels.instance.getWarHandler().getWar(player);

        return war != null && war.contains(player);
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        War war = WarOfSquirrels.instance.getWarHandler().getWar(player);

        if (war.RemovePlayer(player))
            return 1;
        player.getPlayerEntity().sendMessage(ChatText.Error("You cannot leave the war.").withStyle(ChatFormatting.BOLD), Util.NIL_UUID);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You cannot leave the war.").withStyle(ChatFormatting.BOLD);
    }
}
