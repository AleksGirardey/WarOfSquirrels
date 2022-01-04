package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.handler.CuboHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.ChestLocation;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.apache.commons.lang3.tuple.Pair;

public class CitySetChestLocation extends CityMayorOrAssistantCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("upgradeChestLocation").executes(this);
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
            player.sendMessage(ChatText.Error("You cannot set your upgrade chest with those points."));
        } else
            player.sendMessage(ChatText.Error("You need to set points first (use /city cubo)"));
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        CuboHandler handler = WarOfSquirrels.instance.getCuboHandler();
        Pair<Vector3, Vector3> points = handler.getPoints(player);

        player.sendMessage(ChatText.Success("Upgrade chest location now set between " + points.getKey() + " and " + points.getValue()));

        player.getCity().setUpgradeChestLocation(new ChestLocation(points.getLeft(), points.getRight()));
        return 0;
    }
}
