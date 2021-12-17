package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class CityUnClaim extends CityAssistantCommandBuilder {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("unclaim").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        int x = player.getPlayerEntity().chunkPosition().x;
        int z = player.getPlayerEntity().chunkPosition().z;
        ResourceKey<Level> dimensionId = player.getPlayerEntity().getCommandSenderWorld().dimension();

        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(x, z, dimensionId);
        MutableComponent message = ChatText.Error("");

        boolean ret = true;

        if (chunk == null || chunk.getCity() != player.getCity()) {
            message = ChatText.Error("This chunk did not belong to your city.");
            ret = false;
        } else if (chunk.getHomeBlock()) {
            message = ChatText.Error("You cannot un-claim your HomeBlock");
            ret = false;
        }

        player.getPlayerEntity().sendMessage(message, Util.NIL_UUID);
        return ret;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        int x = player.getPlayerEntity().chunkPosition().x;
        int z = player.getPlayerEntity().chunkPosition().z;
        ResourceKey<Level> dimensionId = player.getPlayerEntity().getCommandSenderWorld().dimension();

        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(x, z, dimensionId);

        if (WarOfSquirrels.instance.getChunkHandler().Delete(chunk)) {
            MutableComponent message = ChatText.Success("Chunk '" + chunk.toString() + "' is now unclaimed.");
            WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(player.getCity(), player, message, true);
        }
        return 0;
    }
}