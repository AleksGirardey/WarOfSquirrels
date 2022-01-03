package fr.craftandconquest.warofsquirrels.commands.city.upgrade;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.upgrade.CityUpgrade;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CityUpgradeFill extends CityMayorOrAssistantCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("fill").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city = player.getCity();

        return city.getUpgradeChestLocation() != null;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city = player.getCity();
        BlockPos cornerOne = city.getUpgradeChestLocation().getCornerOne();
        BlockPos cornerTwo = city.getUpgradeChestLocation().getCornerTwo();

        Level level = WarOfSquirrels.server.getLevel(Level.OVERWORLD);

        if (level == null) return -1;

        BlockPos.betweenClosedStream(cornerOne, cornerTwo).forEach(blockPos -> {
            BlockState blockState = level.getBlockState(blockPos);

            if (!(blockState.getBlock() instanceof ChestBlock)) return;

            ChestBlock chestBlock = (ChestBlock) level.getBlockState(blockPos).getBlock();
            Container container = ChestBlock.getContainer(chestBlock, blockState, level, blockPos, false);

            city.getCityUpgrade().FillUpgrade(CityUpgrade.UpgradeType.Level, container);
        });

        return 0;
    }
}
