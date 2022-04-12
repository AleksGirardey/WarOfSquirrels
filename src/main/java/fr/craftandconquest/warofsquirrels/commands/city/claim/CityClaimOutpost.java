package fr.craftandconquest.warofsquirrels.commands.city.claim;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.handler.ChunkHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.ChunkLocation;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import lombok.NoArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;

@NoArgsConstructor
public class CityClaimOutpost extends CityMayorOrAssistantCommandBuilder {
    private final static CityClaimOutpost CMD_NO_ARGS = new CityClaimOutpost(false);
    private final static CityClaimOutpost CMD_ARGS = new CityClaimOutpost(true);

    private final String argumentName = "ChunkName";
    private boolean args = false;

    private CityClaimOutpost(boolean hasArgs) { args = hasArgs; }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("outpost")
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
        City city = player.getCity();

        x = player.getPlayerEntity().chunkPosition().x;
        z = player.getPlayerEntity().chunkPosition().z;

        Vector2 territoryPos = Utils.FromChunkToTerritory(x, z);
        Territory targetTerritory = WarOfSquirrels.instance.getTerritoryHandler().getFromChunkPos(new Vector2(x, z));
        ChunkLocation chunkLocation = new ChunkLocation(x, z, dimensionId);
        Faction playerFaction = player.getCity().getFaction();

        boolean doesChunkExist = chh.exists(x, z, dimensionId);
        boolean isCityTerritory = targetTerritory.equals(city.getTerritory());
        boolean hasFaction = playerFaction != null;
        boolean territoryHasFaction = targetTerritory.getFaction() != null;
        boolean ownTerritory = hasFaction && territoryHasFaction && targetTerritory.getFaction().equals(playerFaction);

        if (doesChunkExist) {
            player.sendMessage(ChatText.Error("Chunk already claimed by someone"));
            return false;
        }

        if (ownTerritory || isCityTerritory) {
            player.sendMessage(ChatText.Error("You cannot place an outpost on your territory"));
            return false;
        }

        List<Chunk> outpostList = chh.getOutpostList(player.getCity());
        int outpostCount = outpostList.size();
        int outpostMax = player.getCity().getMaxOutpost();

        if (outpostCount >= outpostMax) {
            player.sendMessage(ChatText.Error("You reached the maximum outposts available."));
            return false;
        }

        for (Chunk chunk : outpostList) {
            if (territoryPos.equals(Utils.FromChunkToTerritory(chunk.getPosX(), chunk.getPosZ()))) {
                player.sendMessage(ChatText.Error("Your city already got an outpost on this territory"));
                return false;
            }
        }

        boolean canPlaceOutpost = chh.canPlaceOutpost(player.getCity(), chunkLocation);

        if (!canPlaceOutpost) {
            player.sendMessage(ChatText.Error("You are too close from an other homeblock"));
            return false;
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        ChunkHandler chh = WarOfSquirrels.instance.getChunkHandler();
        int x, z;
        ResourceKey<Level> dimensionId = player.getPlayerEntity().getCommandSenderWorld().dimension();
        Chunk chunk;

        x = player.getPlayerEntity().chunkPosition().x;
        z = player.getPlayerEntity().chunkPosition().z;

        if (args)
            chunk = chh.CreateChunk(x, z, player.getCity(), dimensionId, context.getArgument(argumentName, String.class));
        else
            chunk = chh.CreateChunk(x, z, player.getCity(), dimensionId);

        chunk.setOutpost(true);
        chunk.setRespawnPoint(new Vector3(
                player.getPlayerEntity().getBlockX(),
                player.getPlayerEntity().getBlockY(),
                player.getPlayerEntity().getBlockZ()));

        chh.Save();

        return 0;
    }
}
