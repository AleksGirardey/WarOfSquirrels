package fr.craftandconquest.warofsquirrels.commands.reward;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class RewardClaim extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("claim").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (player.getRewards().size() == 0) {
            player.sendMessage(ChatText.Error("You do not have rewards to claim"));
            return false;
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        int size = WarOfSquirrels.instance.getRewardHandler().claim(player);

        player.sendMessage(ChatText.Success("You claimed " + size + " rewards."));

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("Cannot perform command RewardCommandClaim");
    }
}
