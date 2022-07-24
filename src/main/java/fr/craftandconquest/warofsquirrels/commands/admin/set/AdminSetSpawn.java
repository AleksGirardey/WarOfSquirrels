package fr.craftandconquest.warofsquirrels.commands.admin.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.config.ConfigData;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Config;
import fr.craftandconquest.warofsquirrels.utils.ReSpawnPoint;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;

public class AdminSetSpawn extends AdminCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("setSpawn").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (player.getLastDimensionKey().equals(WarOfSquirrels.SPAWN)) return true;

        player.sendMessage(ChatText.Error("You cannot set server spawn in an other dimension than spawn dimension"), true);
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        ConfigData config = WarOfSquirrels.instance.getConfig();

        config.setServerSpawn(new Vector3(
                player.getPlayerEntity().getBlockX(),
                player.getPlayerEntity().getBlockY(),
                player.getPlayerEntity().getBlockZ()));

        ReSpawnPoint.DEFAULT_SPAWN = new ReSpawnPoint(WarOfSquirrels.SPAWN, new BlockPos(config.getServerSpawn().x, config.getServerSpawn().y, config.getServerSpawn().z));

        player.sendMessage(ChatText.Success("Spawn is not set to " + WarOfSquirrels.instance.getConfig().getServerSpawn()));


        return 0;
    }
}
