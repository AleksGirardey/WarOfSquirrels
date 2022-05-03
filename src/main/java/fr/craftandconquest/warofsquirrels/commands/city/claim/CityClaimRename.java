package fr.craftandconquest.warofsquirrels.commands.city.claim;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.handler.ChunkHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import lombok.NoArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

@NoArgsConstructor
public class CityClaimRename extends CityMayorOrAssistantCommandBuilder {
    private final String argumentName = "name";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("rename")
                .then(Commands.argument(argumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        ChunkHandler handler = WarOfSquirrels.instance.getChunkHandler();
        ResourceKey<Level> dimensionId = player.getPlayerEntity().getCommandSenderWorld().dimension();
        City city = player.getCity();

        int x = player.getPlayerEntity().chunkPosition().x;
        int z = player.getPlayerEntity().chunkPosition().z;

        boolean doesChunkExist = handler.exists(x, z, dimensionId);
        boolean sameCity = doesChunkExist && handler.getChunk(x, z, dimensionId).getRelatedCity() == city;

        if (!doesChunkExist) {
            player.sendMessage(ChatText.Error("You need to be on a chunk to perform this command."));
            return false;
        }

        if (!sameCity) {
            player.sendMessage(ChatText.Error("You need to own the chunk to rename it"));
        }

        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        ChunkHandler handler = WarOfSquirrels.instance.getChunkHandler();
        String name = StringArgumentType.getString(context, argumentName);

        int x = player.getPlayerEntity().chunkPosition().x;
        int z = player.getPlayerEntity().chunkPosition().z;
        ResourceKey<Level> dimensionId = player.getPlayerEntity().getCommandSenderWorld().dimension();

        handler.getChunk(x, z, dimensionId).setName(name);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(
                player.getCity(), null,
                ChatText.Success("Chunk [" + x + ";" + z + "] is now named '" + name + "'"), true);

        return 0;
    }
}
