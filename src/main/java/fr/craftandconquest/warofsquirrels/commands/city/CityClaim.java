package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.ChunkHandler;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.CityRank;
import fr.craftandconquest.warofsquirrels.object.world.ChunkLocation;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CityClaim extends CityAssistantCommandBuilder {
    private final CityClaim CMD_NO_ARGS = new CityClaim(false);
    private final CityClaim CMD_ARGS = new CityClaim(true);

    private final String argumentName = "[ChunkName]";

    private final boolean args;

    public CityClaim() { args = false; }
    private CityClaim(boolean hasArgs) { args = hasArgs; }

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("claim")
                .executes(CMD_NO_ARGS)
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(CMD_ARGS));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        ChunkHandler chh = WarOfSquirrels.instance.getChunkHandler();
        int x, z;
        int dimensionId = player.getPlayerEntity().dimension.getId();

        x = player.getPlayerEntity().chunkCoordX;
        z = player.getPlayerEntity().chunkCoordZ;

        if (!chh.exists(x, z, dimensionId)) {
            ChunkLocation chunkLocation = new ChunkLocation(x, z, dimensionId);
            if (chh.canBePlaced(player.getCity(), false, chunkLocation)) {
                CityRank r = player.getCity().getRank();

                if (r.getChunkMax() == chh.getSize(player.getCity())) {
                    player.getPlayerEntity().sendMessage(
                            new StringTextComponent("You reached the maximum chunk available to claim.")
                                    .applyTextStyle(TextFormatting.RED));
                    return false;
                }
                return true;
            } else if (chh.canBePlaced(player.getCity(), true, chunkLocation))
                return true;
        }
        player.getPlayerEntity().sendMessage(
                new StringTextComponent("You can't claim here.").applyTextStyle(TextFormatting.RED));
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        ChunkHandler chh = WarOfSquirrels.instance.getChunkHandler();
        int x, z;
        int dimensionId = player.getPlayerEntity().dimension.getId();

        x = player.getPlayerEntity().chunkCoordX;
        z = player.getPlayerEntity().chunkCoordZ;

        if (args)
            chh.CreateChunk(x, z, player.getCity(), dimensionId, context.getArgument(argumentName, String.class));
        else
            chh.CreateChunk(x, z, player.getCity(), dimensionId);
        return 0;
    }
}
