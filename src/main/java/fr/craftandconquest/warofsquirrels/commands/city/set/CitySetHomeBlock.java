package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.handler.ChunkHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CitySetHomeBlock extends CityMayorOrAssistantCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("homeblock")
                .executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        ChunkHandler chunkHandler = WarOfSquirrels.instance.getChunkHandler();
        Chunk chunk;
        int x, z;

        x = player.getLastChunkX();
        z = player.getLastChunkZ();
        chunk = chunkHandler.getChunk(x, z, player.getPlayerEntity().getCommandSenderWorld().dimension());
        return (chunk != null && chunk.getCity() == player.getCity() && !chunk.getHomeBlock() && !chunk.getOutpost());
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        WarOfSquirrels.instance.getChunkHandler().setHomeBlock(player);
        return 0;
    }
}
