package fr.craftandconquest.warofsquirrels.commands.cubo;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.handler.CuboHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.apache.commons.lang3.tuple.Pair;

public class CuboCreate extends CityMayorOrAssistantCommandBuilder implements IAdminCommand {
    private final String argumentName = "[CuboName]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("create").then(Commands.argument(argumentName, StringArgumentType.string()).executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        CuboHandler handler = WarOfSquirrels.instance.getCuboHandler();

        if (IsAdmin(player)) return true;

        if (handler.playerExists(player)) {
            Pair<Vector3, Vector3> points = handler.getPoints(player);
            if (points.getLeft() != null && points.getRight() != null) {
                Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(points.getLeft(), player.getPlayerEntity().getCommandSenderWorld().dimension());
                if (chunk != null) {
                    City city = chunk.getRelatedCity();
                    if (city == player.getCity()) {
                        chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(points.getRight(), player.getPlayerEntity().getCommandSenderWorld().dimension());
                        if (chunk != null) {
                            city = chunk.getRelatedCity();
                            return city == player.getCity();
                        }
                    }
                }
            }
            player.sendMessage(ChatText.Error("You cannot create a cubo with those bounds"));
        } else
            player.sendMessage(ChatText.Error("You need to define boundaries first (using /cubo)"));
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        WarOfSquirrels.instance.getCuboHandler().CreateCubo(player, context.getArgument(argumentName, String.class));
        WarOfSquirrels.instance.getCuboHandler().Save();
        return 0;
    }
}
