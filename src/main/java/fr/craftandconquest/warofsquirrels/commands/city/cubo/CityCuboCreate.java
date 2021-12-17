package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.handler.CuboHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.apache.commons.lang3.tuple.Pair;

public class CityCuboCreate extends CityAssistantCommandBuilder {
    private final String argumentName = "[CuboName]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("create").then(Commands.argument(argumentName, StringArgumentType.string()).executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        CuboHandler handler = WarOfSquirrels.instance.getCuboHandler();

        if (handler.playerExists(player)) {
            Pair<Vector3, Vector3> points = handler.getPoints(player);
            if (points.getLeft() != null && points.getRight() != null) {
                Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(points.getLeft(), player.getPlayerEntity().getCommandSenderWorld().dimension());
                if (chunk != null) {
                    City city = chunk.getCity();
                    if (city == player.getCity()) {
                        chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(points.getRight(), player.getPlayerEntity().getCommandSenderWorld().dimension());
                        if (chunk != null) {
                            city = chunk.getCity();
                            return city == player.getCity();
                        }
                    }
                }
            }
            player.getPlayerEntity().sendMessage(ChatText.Error("Vous ne pouvez pas définir de cubo avec des limites."), Util.NIL_UUID);
        } else
            player.getPlayerEntity().sendMessage(ChatText.Error("Vous devez dans un premier temps définir les limites du cubo (/city cubo)"), Util.NIL_UUID);
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        WarOfSquirrels.instance.getCuboHandler().CreateCubo(player, context.getArgument(argumentName, String.class));
        return 0;
    }
}
