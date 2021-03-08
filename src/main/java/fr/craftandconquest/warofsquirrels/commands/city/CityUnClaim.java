package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.ChunkHandler;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.CityRank;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.ChunkLocation;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CityUnClaim extends CityAssistantCommandBuilder {

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("unclaim").executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        int x = player.getPlayerEntity().chunkCoordX;
        int z = player.getPlayerEntity().chunkCoordZ;
        int dimensionId = player.getPlayerEntity().dimension.getId();

        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(x, z, dimensionId);
        StringTextComponent message = new StringTextComponent("");

        boolean         ret = true;

        if (chunk == null || chunk.getCity() != player.getCity()) {
            message.appendText("This chunk did not belong to your city.").applyTextStyle(TextFormatting.RED);
            ret = false;
        } else if (chunk.getHomeBlock()) {
            message.appendText("You cannot un-claim your HomeBlock");
            ret = false;
        }

        player.getPlayerEntity().sendMessage(message);
        return ret;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        int x = player.getPlayerEntity().chunkCoordX;
        int z = player.getPlayerEntity().chunkCoordZ;
        int dimensionId = player.getPlayerEntity().dimension.getId();

        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(x, z, dimensionId);

        if (WarOfSquirrels.instance.getChunkHandler().Delete(chunk)) {
            StringTextComponent message = new StringTextComponent("Chunk '" + chunk.toString() + "' is now unclaimed.");
            message.applyTextStyle(TextFormatting.GREEN);
            WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(player.getCity(), player, message, true);
        }
        return 0;
    }
}