package fr.craftandconquest.warofsquirrels.commands.admin.create;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IAdminCuboExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.admin.CustomReward;
import fr.craftandconquest.warofsquirrels.object.upgrade.UpgradeItem;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;

import java.util.Collections;

public class AdminCreateReward extends AdminCommandBuilder implements IAdminCuboExtractor {
    private static final String rewardedQuantityName = "rewardedQuantity";
    private static final String rewardName = "rewardedQuantity";
    private static final String rewardQuantityName = "quantity";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() { return null; }

    public LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext context) {
        return Commands.literal("reward")
                .then(getArgumentRegister()
                        .then(Commands.argument(rewardedQuantityName, IntegerArgumentType.integer())
                                .then(Commands.argument(rewardName, ItemArgument.item(context))
                                        .then(Commands.argument(rewardQuantityName, IntegerArgumentType.integer())))));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (getArgument(context) == null) {
            player.sendMessage(ChatText.Error("There is no AdminCubo named '" + getRawArgument(context) + "'"));
            return false;
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        CustomReward reward = new CustomReward(
                getArgument(context).getUuid(),
                IntegerArgumentType.getInteger(context, rewardedQuantityName),
                new UpgradeItem(ItemArgument.getItem(context, rewardName).getItem()),
                IntegerArgumentType.getInteger(context, rewardQuantityName),
                Collections.emptyList(),
                Collections.emptyList());

        WarOfSquirrels.instance.getRewardHandler().add(reward);

        player.sendMessage(ChatText.Success("Reward created !"));

        return 0;
    }
}
