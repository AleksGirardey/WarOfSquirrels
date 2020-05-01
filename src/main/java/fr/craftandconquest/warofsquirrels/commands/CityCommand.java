package fr.craftandconquest.warofsquirrels.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;

public class CityCommand implements Command<CommandSource> {

    private static final CityCommand CMD = new CityCommand();

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("city")
                .then(Commands
                        .argument("cityName", StringArgumentType.string())
                        .executes(CMD)));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity playerEntity = context.getSource().asPlayer();
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity);
        String cityName = context.getArgument("cityName", String.class);

        City city = WarOfSquirrels.instance.getCityHandler().CreateCity(
                cityName,
                cityName.substring(0, Math.min(cityName.length(), 3)),
                player);

        player.setCity(city);
        WarOfSquirrels.instance.getPlayerHandler().Save();

        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().CreateChunk(
                playerEntity.chunkCoordX,
                playerEntity.chunkCoordZ,
                city,
                playerEntity.dimension.getId());

        chunk.setHomeBlock(true);
        chunk.setRespawnX(playerEntity.getPosition().getX());
        chunk.setRespawnY(playerEntity.getPosition().getY());
        chunk.setRespawnZ(playerEntity.getPosition().getZ());

        WarOfSquirrels.instance.getChunkHandler().Save();

        return 1;
    }
}
