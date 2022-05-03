package fr.craftandconquest.warofsquirrels.commands.city.claim;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.handler.ChunkHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.world.ChunkLocation;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import lombok.NoArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

@NoArgsConstructor
public class CityClaimChunk extends CityMayorOrAssistantCommandBuilder {
    private final static CityClaimChunk CMD_NO_ARGS = new CityClaimChunk(false);
    private final static CityClaimChunk CMD_ARGS = new CityClaimChunk(true);

    private final String argumentName = "ChunkName";
    private boolean args = false;

    private CityClaimChunk(boolean hasArgs) { args = hasArgs; }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("chunk")
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

        Territory targetTerritory = WarOfSquirrels.instance.getTerritoryHandler().getFromChunkPos(new Vector2(x, z));
        ChunkLocation chunkLocation = new ChunkLocation(x, z, dimensionId);

        int chunkSize = targetTerritory.getFortification().getLinkedChunkSize();
        int maxChunk = targetTerritory.getFortification().getMaxChunk();

        boolean isCityTerritory = targetTerritory.equals(city.getTerritory());
        boolean doesChunkExist = chh.exists(x, z, dimensionId);
        boolean canPlaceChunk = !doesChunkExist && chh.canPlaceChunk(targetTerritory, player.getCity(), chunkLocation);
        boolean hasChunkSlotLeft = maxChunk > chunkSize;

        if (!isCityTerritory) {
            player.sendMessage(ChatText.Error("You cannot claim a chunk which is not in your home territory."));
            return false;
        }

        if (canPlaceChunk) {
            if (!hasChunkSlotLeft) {
                player.sendMessage(ChatText.Error("You have reached the maximum chunk available to claim (" + chunkSize + "/" + maxChunk + ")"));
                return false;
            }
            return true;
        }

        player.sendMessage(ChatText.Error("You cannot claim this chunk for your city."));
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
            chh.CreateChunk(x, z, player.getCity(), dimensionId, StringArgumentType.getString(context, argumentName));
        else
            chh.CreateChunk(x, z, player.getCity(), dimensionId);

        chh.Save();

        return 0;
    }
}
