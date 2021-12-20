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

        if (chunk != null && chunk.getCity().equals(player.getCity()) && (chunk.getHomeBlock() || chunk.getOutpost())) {
            return true;
        }
        player.getPlayerEntity().sendMessage(ChatText.Error("You cannot set your spawn on this chunk."), Util.NIL_UUID);
        return true;
    }

    @Override
    protected boolean CanDoIt(FullPlayer player) {
        if (player.getAssistant()) return super.CanDoIt(player);
        else {
            if (player.getCity() == null) return false;
            return player.getCity().getOwner().equals(player);
        }
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        int x, y, z;
        Chunk chunk;

        x = player.getPlayerEntity().getBlockX();
        y = player.getPlayerEntity().getBlockY();
        z = player.getPlayerEntity().getBlockZ();

        ChunkPos chunkPos = Utils.WorldToChunkPos(x, z);

        chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(chunkPos.x, chunkPos.z, player.getPlayerEntity().getCommandSenderWorld().dimension());
        chunk.setRespawnX(x);
        chunk.setRespawnY(y);
        chunk.setRespawnZ(z);

        MutableComponent message = ChatText.Success(chunk + " has now a spawn point at [" + x + ";" + y + ";" + z + "]");

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(player.getCity(), null, message, true);

        return 0;
    }
}
