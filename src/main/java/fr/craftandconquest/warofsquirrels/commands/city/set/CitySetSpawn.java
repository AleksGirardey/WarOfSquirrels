package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.ChunkPos;

public class CitySetSpawn extends CityMayorOrAssistantCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("spawn").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(
                player.getPlayerEntity().chunkPosition().x,
                player.getPlayerEntity().chunkPosition().z,
                player.getPlayerEntity().getCommandSenderWorld().dimension());

//        WarOfSquirrels.LOGGER.info("[WoS][Debug] " + (chunk == null ? "NULL":"CHUNK") + " - " + (!chunk.getCity().equals(player.getCity()) ? "NONE":"EQUAL") + " - " + (chunk.getHomeBlock() ? "HB" : "NONE") + " - " + (chunk.getOutpost() ? "Outpost" : "NONE"));

        if (chunk != null && chunk.getRelatedCity().equals(player.getCity()) && (chunk.getHomeBlock() || chunk.getOutpost())) {
            return true;
        }
        player.sendMessage(ChatText.Error("You cannot set your fortification spawn on this chunk."));
        return false;
    }
    
    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        int x, y, z;
        Chunk chunk;

        x = player.getPlayerEntity().getBlockX();
        y = player.getPlayerEntity().getBlockY();
        z = player.getPlayerEntity().getBlockZ();

        ChunkPos chunkPos = Utils.FromWorldToChunkPos(x, z);

        chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(chunkPos.x, chunkPos.z, player.getPlayerEntity().getCommandSenderWorld().dimension());
        chunk.setRespawnPoint(new Vector3(x, y, z));

        MutableComponent message = ChatText.Success(chunk + " has now a spawn point at [" + x + ";" + y + ";" + z + "]");

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(player.getCity(), null, message, true);

        return 0;
    }
}
