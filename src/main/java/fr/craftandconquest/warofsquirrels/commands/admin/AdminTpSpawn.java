package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.SpawnTeleporter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

public class AdminTpSpawn extends AdminCommandBuilder implements IPlayerExtractor {
    private final static AdminTpSpawn CMD_NO_ARGS = new AdminTpSpawn(false);
    private final static AdminTpSpawn CMD_ARGS = new AdminTpSpawn(true);

    private final boolean args;

    public AdminTpSpawn(boolean hasArgs) { args = hasArgs; }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("tpSpawn")
                .executes(CMD_NO_ARGS)
                .then(Commands
                        .argument(playerNameArgument, EntityArgument.player())
                        .executes(CMD_ARGS));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Player target = player.getPlayerEntity();

        if (args)
            target = getPlayer(context).getPlayerEntity();

        SpawnTeleporter teleporter = new SpawnTeleporter(0, 64, 0);

        ServerLevel level = WarOfSquirrels.server.getLevel(WarOfSquirrels.SPAWN);

        if (level == null) {
            player.sendMessage(ChatText.Error("Error while teleporting player"));
            return 0;
        }

        target.changeDimension(level, teleporter);

        return 0;
    }
}
