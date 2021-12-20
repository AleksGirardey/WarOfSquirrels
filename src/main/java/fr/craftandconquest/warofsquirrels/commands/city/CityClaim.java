package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.ChunkHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.CityRank;
import fr.craftandconquest.warofsquirrels.object.world.ChunkLocation;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.text.MessageFormat;

public class CityClaim extends CityMayorOrAssistantCommandBuilder {
    private final static CityClaim CMD_NO_ARGS = new CityClaim(false);
    private final static CityClaim CMD_ARGS = new CityClaim(true);

    private final String argumentName = "[ChunkName]";

    private final boolean args;

    public CityClaim() {
        args = false;
    }

    private CityClaim(boolean hasArgs) {
        args = hasArgs;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("claim")
                .executes(CMD_NO_ARGS)
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(CMD_ARGS));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        ChunkHandler chh = WarOfSquirrels.instance.getChunkHandler();
        int x, z;
        ResourceKey<Level> dimensionId = player.getPlayerEntity().getCommandSenderWorld().dimension();

        x = player.getPlayerEntity().chunkPosition().x;
        z = player.getPlayerEntity().chunkPosition().z;

        if (!chh.exists(x, z, dimensionId)) {
            ChunkLocation chunkLocation = new ChunkLocation(x, z, dimensionId);
            if (chh.canBePlaced(player.getCity(), false, chunkLocation)) {
                CityRank r = player.getCity().getRank();

                if (r.getChunkMax() == chh.getSize(player.getCity())) {
                    player.getPlayerEntity().sendMessage(
                            ChatText.Error("You reached the maximum chunk available to claim."), Util.NIL_UUID);
                    return false;
                }
                return true;
            } else if (chh.canBePlaced(player.getCity(), true, chunkLocation))
                return true;
        }
        player.getPlayerEntity().sendMessage(
                ChatText.Error("You can't claim here."), Util.NIL_UUID);
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        ChunkHandler chh = WarOfSquirrels.instance.getChunkHandler();
        int x, z;
        ResourceKey<Level> dimensionId = player.getPlayerEntity().getCommandSenderWorld().dimension();

        x = player.getPlayerEntity().chunkPosition().x;
        z = player.getPlayerEntity().chunkPosition().z;

        if (args)
            chh.CreateChunk(x, z, player.getCity(), dimensionId, context.getArgument(argumentName, String.class));
        else
            chh.CreateChunk(x, z, player.getCity(), dimensionId);
        return 0;
    }
}
