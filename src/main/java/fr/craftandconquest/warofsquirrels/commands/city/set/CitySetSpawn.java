package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CitySetSpawn extends CityAssistantCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("spawn").executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(
                player.getPlayerEntity().chunkCoordX,
                player.getPlayerEntity().chunkCoordZ,
                player.getPlayerEntity().dimension.getId());

        if (chunk == null || chunk.getCity() != player.getCity() || !chunk.getHomeBlock() || !chunk.getOutpost()) {
            StringTextComponent message = new StringTextComponent("You cannot set your spawn on this chunk.");
            message.applyTextStyle(TextFormatting.RED);
            player.getPlayerEntity().sendMessage(message);
            return false;
        }
        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        int                     x, y, z;
        Chunk                   chunk;

        x = player.getPlayerEntity().getPosition().getX();
        y = player.getPlayerEntity().getPosition().getY();
        z = player.getPlayerEntity().getPosition().getZ();

        chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(x / 16, z / 16, player.getPlayerEntity().dimension.getId());
        chunk.setRespawnX(x);
        chunk.setRespawnY(y);
        chunk.setRespawnZ(z);

        StringTextComponent message = new StringTextComponent(chunk.toString() + " has now a spawn point at [" + x + ";" + y + ";" + z + "]");
        message.applyTextStyle(TextFormatting.GREEN);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(player.getCity(), null, message, true);

        return 0;
    }
}
