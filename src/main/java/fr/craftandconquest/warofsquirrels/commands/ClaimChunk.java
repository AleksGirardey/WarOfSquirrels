package fr.craftandconquest.warofsquirrels.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class ClaimChunk implements Command<CommandSource> {

    private static final ClaimChunk CMD = new ClaimChunk();

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("ClaimChunk")
                .then(Commands.argument("cityName", StringArgumentType.string()).executes(CMD)));

        dispatcher.register(Commands.literal("ChunkList")
                .executes(context -> {
                    context.getSource().sendFeedback(new StringTextComponent(WarOfSquirrels.instance.chunkHandler.getListAsString()), true);
                    return 1;
                }));
    }

    @Override
    public int run(CommandContext<CommandSource> context) {
        Chunk chunk = new Chunk(
                context.getSource().getPos().x,
                context.getSource().getPos().z,
                context.getArgument("cityName", String.class),
                context.getSource().getWorld().getDimension().getType().getId());

        if (WarOfSquirrels.instance.chunkHandler.CreateChunk(chunk))
            return 1;
        context.getSource().sendFeedback(new StringTextComponent("Couldn't claim this chunk"), true);
        return 0;
    }
}
