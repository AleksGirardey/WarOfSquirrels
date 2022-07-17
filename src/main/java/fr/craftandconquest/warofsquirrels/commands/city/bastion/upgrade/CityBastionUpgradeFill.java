package fr.craftandconquest.warofsquirrels.commands.city.bastion.upgrade;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityBastionCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.upgrade.bastion.BastionUpgrade;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
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
public class CityBastionUpgradeFill extends CityBastionCommandBuilder {
    private String upgradeTarget;
    private BastionUpgrade.UpgradeType upgradeType;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal(upgradeTarget).executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Vector2 territoryPos = Utils.FromWorldToTerritory(player.getPlayerEntity().getBlockX(), player.getPlayerEntity().getBlockZ());
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().getFromTerritoryPos(territoryPos);
        Bastion bastion = (Bastion) territory.getFortification();

        if (bastion.getUpgradeChestLocation() == null) {
            player.sendMessage(ChatText.Error("You need to set your bastion's upgrade chest. (/city bastion upgradeChestLocation)"));

        }

        if (!bastion.getBastionUpgrade().CanFill(upgradeType)) {
            player.sendMessage(ChatText.Error("You cannot upgrade this; Check if you got the correct bastion level."));
            return false;
        }
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Vector2 territoryPos = Utils.FromWorldToTerritory(player.getPlayerEntity().getBlockX(), player.getPlayerEntity().getBlockZ());
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().getFromTerritoryPos(territoryPos);
        Bastion bastion = (Bastion) territory.getFortification();
        BlockPos cornerOne = bastion.getUpgradeChestLocation().getCornerOne();
        BlockPos cornerTwo = bastion.getUpgradeChestLocation().getCornerTwo();
        AtomicInteger amount = new AtomicInteger();

        Level level = WarOfSquirrels.server.getLevel(Level.OVERWORLD);

        if (level == null) return -1;

        BlockPos.betweenClosedStream(cornerOne, cornerTwo).forEach(blockPos -> {
            BlockState blockState = level.getBlockState(blockPos);

            if (!(blockState.getBlock() instanceof ChestBlock)) return;

            ChestBlock chestBlock = (ChestBlock) level.getBlockState(blockPos).getBlock();
            Container container = ChestBlock.getContainer(chestBlock, blockState, level, blockPos, false);

            amount.addAndGet(bastion.getBastionUpgrade().FillUpgrade(upgradeType, container));
        });

        player.sendMessage(ChatText.Success("Upgrade " + upgradeType + " has been filled with " + amount + " blocks or items."));

        return 0;
    }
}
