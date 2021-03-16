package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.handler.CuboHandler;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Pair;

public class CityCuboCreate extends CityAssistantCommandBuilder {
    private final String argumentName = "[CuboName]";
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("create").then(Commands.argument(argumentName, StringArgumentType.string()).executes(this));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        CuboHandler handler = WarOfSquirrels.instance.getCuboHandler();

        if (handler.playerExists(player)) {
            Pair<Vector3, Vector3> points = handler.getPoints(player);
            if (points.getLeft() != null && points.getRight() != null) {
                Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(points.getLeft(), player.getPlayerEntity().dimension.getId());
                if (chunk != null) {
                    City city = chunk.getCity();
                    if (city == player.getCity()) {
                        chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(points.getRight(), player.getPlayerEntity().dimension.getId());
                        if (chunk != null) {
                            city = chunk.getCity();
                            return city == player.getCity();
                        }
                    }
                }
            }
            player.getPlayerEntity().sendMessage(new StringTextComponent("Vous ne pouvez pas définir de cubo avec des limites.").applyTextStyle(TextFormatting.RED));
        } else
            player.getPlayerEntity().sendMessage(new StringTextComponent("Vous devez dans un premier temps définir les limites du cubo (/city cubo)").applyTextStyle(TextFormatting.RED));
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        WarOfSquirrels.instance.getCuboHandler().CreateCubo(player, context.getArgument(argumentName, String.class));
        return 0;
    }
}
