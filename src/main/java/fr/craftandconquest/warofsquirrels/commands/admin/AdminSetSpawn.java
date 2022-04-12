package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminSetSpawn extends AdminCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("setSpawn").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (player.getLastDimensionKey().equals(WarOfSquirrels.SPAWN)) return true;

        player.sendMessage(ChatText.Error("You cannot set server spawn in an other dimension than spawn dimension"));
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        WarOfSquirrels.instance.getConfig().setServerSpawn(new Vector3(
                player.getPlayerEntity().getBlockX(),
                player.getPlayerEntity().getBlockY(),
                player.getPlayerEntity().getBlockZ()));
        return 0;
    }
}
