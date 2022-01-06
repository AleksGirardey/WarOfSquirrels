package fr.craftandconquest.warofsquirrels.commands.city.upgrade;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.upgrade.CityUpgrade;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import lombok.AllArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class CityUpgradeFill extends CityMayorOrAssistantCommandBuilder {
    private String upgradeTarget;
    private CityUpgrade.UpgradeType upgradeType;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal(upgradeTarget).executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city = player.getCity();

        if (city.getUpgradeChestLocation() == null) {
            player.sendMessage(ChatText.Error("You need to set your city's upgrade chest. (/city set upgradeChestLocation)"));
            return false;
        }

        if (!city.getCityUpgrade().CanFill(upgradeType)) {
            player.sendMessage(ChatText.Error("You cannot upgrade this; Check if you got the correct city level."));
            return false;
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city = player.getCity();
        BlockPos cornerOne = city.getUpgradeChestLocation().getCornerOne();
        BlockPos cornerTwo = city.getUpgradeChestLocation().getCornerTwo();
        AtomicInteger amount = new AtomicInteger();

        Level level = WarOfSquirrels.server.getLevel(Level.OVERWORLD);

        if (level == null) return -1;

        BlockPos.betweenClosedStream(cornerOne, cornerTwo).forEach(blockPos -> {
            BlockState blockState = level.getBlockState(blockPos);

            if (!(blockState.getBlock() instanceof ChestBlock)) return;

            ChestBlock chestBlock = (ChestBlock) level.getBlockState(blockPos).getBlock();
            Container container = ChestBlock.getContainer(chestBlock, blockState, level, blockPos, false);

            amount.addAndGet(city.getCityUpgrade().FillUpgrade(upgradeType, container));
        });

        player.sendMessage(ChatText.Success("Upgrade " + upgradeType + " has been filled with " + amount + " blocks or items."));

        return 0;
    }
}
