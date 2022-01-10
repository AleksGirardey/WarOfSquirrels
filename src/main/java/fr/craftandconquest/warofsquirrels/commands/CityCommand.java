package fr.craftandconquest.warofsquirrels.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class CityCommand implements Command<CommandSourceStack> {

    private static final CityCommand CMD = new CityCommand();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("city")
                .then(Commands
                        .argument("cityName", StringArgumentType.string())
                        .executes(CMD)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player playerEntity = context.getSource().getPlayerOrException();
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());
        String cityName = context.getArgument("cityName", String.class);

        City city = WarOfSquirrels.instance.getCityHandler().CreateCity(
                cityName,
                cityName.substring(0, Math.min(cityName.length(), 3)),
                player);

        player.setCity(city);
        WarOfSquirrels.instance.getPlayerHandler().Save();

        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().CreateChunk(
                playerEntity.chunkPosition().x,
                playerEntity.chunkPosition().z,
                city,
                playerEntity.getCommandSenderWorld().dimension());

        chunk.setHomeBlock(true);
        chunk.setRespawnPoint(new Vector3(playerEntity.getBlockX(), playerEntity.getBlockY(), playerEntity.getBlockZ()));

        WarOfSquirrels.instance.getChunkHandler().Save();

        return 1;
    }
}
