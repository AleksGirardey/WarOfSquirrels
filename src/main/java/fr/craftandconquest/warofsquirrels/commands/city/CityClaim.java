package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.ChunkHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.faction.city.CityRank;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.ChunkLocation;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Pair;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;

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

        Pair<Integer, Integer> territoryPos = Utils.ChunkToTerritoryCoordinates(x, z);
        Territory targetTerritory = WarOfSquirrels.instance.getTerritoryHandler().get(Utils.ChunkToTerritoryCoordinatesVector(x, z));

        if (!chh.exists(x, z, dimensionId)) {
            ChunkLocation chunkLocation = new ChunkLocation(x, z, dimensionId);
            if (chh.canPlaceChunk(targetTerritory, player.getCity(), chunkLocation)) {
                int chunkSize = targetTerritory.getFortification().getLinkedChunkSize();
                int maxChunk = targetTerritory.getFortification().getMaxChunk();

                if (chunkSize >= maxChunk) {
                    player.sendMessage(ChatText.Error("You reached the maximum chunk available to claim on this territory."));
                    return false;
                }
            } else {
                if (targetTerritory.getFaction() != null && player.getCity().getFaction() != null && targetTerritory.getFaction().equals(player.getCity().getFaction())) {
                    player.sendMessage(ChatText.Error("You cannot place an outpost on your territory"));
                    return false;
                }
                int outpostCount = chh.getOutpostSize(player.getCity());
                List<Chunk> outpostList = chh.getOutpostList(player.getCity());

                if (outpostCount >= player.getCity().getMaxOutpost()) {
                    player.sendMessage(ChatText.Error("You reached the maximum outposts available."));
                    return false;
                }

                for (Chunk chunk : outpostList) {
                    if (territoryPos.equals(Utils.ChunkToTerritoryCoordinates(chunk.getPosX(), chunk.getPosZ()))) {
                        player.sendMessage(ChatText.Error("Your city already got an outpost on this territory"));
                        return false;
                    }
                }

                if (!chh.canPlaceOutpost(player.getCity(), chunkLocation)) {
                    player.sendMessage(ChatText.Error("You are too close from an other homeblock"));
                    return false;
                }
            }
            return true;
        }
        player.sendMessage(ChatText.Error("You can't claim here."));
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        ChunkHandler chh = WarOfSquirrels.instance.getChunkHandler();
        int x, z;
        ResourceKey<Level> dimensionId = player.getPlayerEntity().getCommandSenderWorld().dimension();

        x = player.getPlayerEntity().chunkPosition().x;
        z = player.getPlayerEntity().chunkPosition().z;
        Territory targetTerritory = WarOfSquirrels.instance.getTerritoryHandler().get(Utils.ChunkToTerritoryCoordinatesVector(x, z));

        ChunkLocation chunkLocation = new ChunkLocation(x, z, dimensionId);
        Chunk chunk;
        boolean isOutpost;
        IFortification targetFortification;

        if (!chh.canPlaceChunk(targetTerritory, player.getCity(), chunkLocation)) {
            isOutpost = true;
            targetFortification = player.getCity();
        } else {
            isOutpost = false;
            targetFortification = targetTerritory.getFortification();
        }

        if (args)
            chh.CreateChunk(x, z, targetFortification, dimensionId, context.getArgument(argumentName, String.class))
                    .setOutpost(isOutpost);
        else
            chh.CreateChunk(x, z, targetFortification, dimensionId)
                    .setOutpost(isOutpost);

        return 0;
    }
}
