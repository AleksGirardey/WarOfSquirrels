package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.reward.RewardClaim;
import fr.craftandconquest.warofsquirrels.commands.reward.RewardReset;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.admin.CustomReward;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class RewardCommandRegister extends CommandBuilder implements ICommandRegister {
    private final static RewardClaim rewardClaim = new RewardClaim();
    private final static RewardReset rewardReset = new RewardReset();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("reward")
                .executes(this)
                .then(rewardClaim.register())
                .then(rewardReset.register()));
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return null;
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        MutableComponent message = ChatText.Success("=== Rewards ===\n");
        List<CustomReward> rewards = WarOfSquirrels.instance.getRewardHandler().getAll(player.getLastDimensionKey());

        message.append(" - There is " + rewards.size() + " rewards available in this world." +
                "\nYou can access " + (int) rewards.stream().filter(reward -> !reward.getRewardedPlayers().contains(player) && reward.CanAddRewardedPlayer()).count() + " of them.");
        message.append(" - You can claim " + player.getRewards().size() + " using /reward claim");
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("Could not run reward command");
    }
}
