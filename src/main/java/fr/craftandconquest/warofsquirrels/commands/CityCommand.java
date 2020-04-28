package fr.craftandconquest.warofsquirrels.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.city.City;
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
        String cityName = context.getArgument("cityName", String.class);

        City city = WarOfSquirrels.instance.getCityHandler().CreateCity(
                cityName,
                cityName.substring(0, 2),
                WarOfSquirrels.instance.getPlayerHandler().get(playerEntity));

        WarOfSquirrels.instance.getChunkHandler().CreateChunk(
                playerEntity.chunkCoordX,
                playerEntity.chunkCoordZ,
                city,
                playerEntity.dimension.getId());

        return 1;
    }
}
