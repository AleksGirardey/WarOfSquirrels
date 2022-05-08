package fr.craftandconquest.warofsquirrels.commands.admin.territory;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.arguments.TerritoryArgumentType;
import fr.craftandconquest.warofsquirrels.commands.extractor.ITerritoryExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.SpawnTeleporter;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.List;

public class AdminTerritoryTp extends AdminCommandBuilder implements ITerritoryExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("tp")
                .then(getTerritoryRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Territory territory = getTerritory(context);

        if (territory == null) {
            player.sendMessage(ChatText.Error("Territory '" + getRawTerritory(context) + "' does not exist"));
            return false;
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Territory territory = getTerritory(context);
        SpawnTeleporter tp = new SpawnTeleporter(Utils.FromTerritoryToWorld(territory.getPosX(), territory.getPosZ()));
        ResourceKey<Level> dim = Level.OVERWORLD;
        ServerLevel level = WarOfSquirrels.server.getLevel(dim);

        if (level == null) {
            player.sendMessage(ChatText.Error("Error while teleporting player"));
            return 0;
        }

        player.getPlayerEntity().changeDimension(level, tp);

        return 0;
    }
}
