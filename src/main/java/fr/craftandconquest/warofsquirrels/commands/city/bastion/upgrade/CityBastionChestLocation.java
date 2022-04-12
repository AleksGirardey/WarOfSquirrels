package fr.craftandconquest.warofsquirrels.commands.city.bastion.upgrade;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityBastionCommandBuilder;
import fr.craftandconquest.warofsquirrels.handler.CuboHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.faction.city.ChestLocation;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class CityBastionChestLocation extends CityBastionCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("upgradeChestLocation").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Vector2 territoryPos = Utils.FromWorldToTerritory(player.getPlayerEntity().getBlockX(), player.getPlayerEntity().getBlockZ());
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().getFromTerritoryPos(territoryPos);
        Bastion bastion = (Bastion) territory.getFortification();
        CuboHandler handler = WarOfSquirrels.instance.getCuboHandler();

        if (handler.playerExists(player)) {
            Pair<Vector3, Vector3> points = handler.getPoints(player);

            if (points.getLeft() != null && points.getRight() != null) {
                List<Chunk> list = WarOfSquirrels.instance.getChunkHandler().getChunks(bastion);
                Chunk chunkPointA = WarOfSquirrels.instance.getChunkHandler().getChunk(points.getKey(), Level.OVERWORLD);
                Chunk chunkPointB = WarOfSquirrels.instance.getChunkHandler().getChunk(points.getLeft(), Level.OVERWORLD);

                if (list.contains(chunkPointA) && list.contains(chunkPointB))
                    return true;
                player.sendMessage(ChatText.Error("Points selected are not in Bastion chunks"));
            }
        } else
            player.sendMessage(ChatText.Error("You need to set points first (use /cubo)"));
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Vector2 territoryPos = Utils.FromWorldToTerritory(player.getPlayerEntity().getBlockX(), player.getPlayerEntity().getBlockZ());
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().getFromTerritoryPos(territoryPos);
        Bastion bastion = (Bastion) territory.getFortification();
        CuboHandler handler = WarOfSquirrels.instance.getCuboHandler();
        Pair<Vector3, Vector3> points = handler.getPoints(player);

        player.sendMessage(ChatText.Success("Upgrade chest location now set between " + points.getKey() + " and " + points.getValue()));

        bastion.setUpgradeChestLocation(new ChestLocation(points.getLeft(), points.getRight()));

        return 0;
    }
}
