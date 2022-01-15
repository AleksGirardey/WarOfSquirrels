package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.teleports.AdminTpDelete;
import fr.craftandconquest.warofsquirrels.commands.admin.teleports.AdminTpLink;
import fr.craftandconquest.warofsquirrels.commands.admin.teleports.AdminTpList;
import fr.craftandconquest.warofsquirrels.commands.admin.teleports.AdminTpSetSpawn;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.SpawnTeleporter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class AdminTp extends AdminCommandBuilder {
    private final AdminTpList adminTpList = new AdminTpList();
    private final AdminTpDelete adminTpDelete = new AdminTpDelete();
    private final AdminTpSetSpawn adminTpSetSpawn = new AdminTpSetSpawn();
    private final AdminTpLink adminTpLink = new AdminTpLink();

    private final String tpNameArgument = "tpName";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("tp")
                .then(Commands.argument(tpNameArgument, StringArgumentType.string()).executes(this))
                .then(adminTpList.register())
                .then(adminTpDelete.register())
                .then(adminTpSetSpawn.register())
                .then(adminTpLink.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String name = context.getArgument(tpNameArgument, String.class);
        AdminCubo cubo = WarOfSquirrels.instance.getAdminHandler().get(name);
        City city = WarOfSquirrels.instance.getCityHandler().getCity(name);

        if (cubo != null && cubo.isTeleporter()) return true;
        if (city != null) return true;

        player.sendMessage(ChatText.Error("Wrong argument"));

        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String name = context.getArgument(tpNameArgument, String.class);
        AdminCubo cubo = WarOfSquirrels.instance.getAdminHandler().get(name);
        City city = WarOfSquirrels.instance.getCityHandler().getCity(name);
        SpawnTeleporter tp;
        ResourceKey<Level> dim;

        if (cubo != null) {
            tp = new SpawnTeleporter(cubo.getRespawnPoint());
            dim = cubo.getDimensionKey();
        } else if (city != null) {
            Chunk hb = city.getHomeBlock();
            tp = new SpawnTeleporter(hb.getRespawnPoint());
            dim = Level.OVERWORLD;
        } else {
            return 0;
        }

        ServerLevel level = WarOfSquirrels.server.getLevel(dim);

        if (level == null) {
            player.sendMessage(ChatText.Error("Error while teleporting player"));
            return 0;
        }

        player.getPlayerEntity().changeDimension(level, tp);

        return 0;
    }
}
