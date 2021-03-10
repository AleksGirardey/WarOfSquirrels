package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.handler.ChunkHandler;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CitySetHomeBlock extends CityAssistantCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("homeblock")
                .executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        ChunkHandler chunkHandler = WarOfSquirrels.instance.getChunkHandler();
        Chunk chunk;
        int x, z;

        x = player.getLastChunkX();
        z = player.getLastChunkZ();
        chunk = chunkHandler.getChunk(x, z, player.getPlayerEntity().dimension.getId());
        return (chunk != null && chunk.getCity() == player.getCity() && !chunk.getHomeBlock() && !chunk.getOutpost());
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        WarOfSquirrels.instance.getChunkHandler().setHomeBlock(player);
        return 0;
    }
}
