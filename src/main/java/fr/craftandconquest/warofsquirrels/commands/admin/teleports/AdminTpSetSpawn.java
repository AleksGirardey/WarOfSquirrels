package fr.craftandconquest.warofsquirrels.commands.admin.teleports;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class AdminTpSetSpawn extends AdminCommandBuilder {
    private final String argumentTpName = "tpName";
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("setSpawn")
                .then(Commands
                        .argument(argumentTpName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String name = context.getArgument(argumentTpName, String.class);
        AdminCubo cubo = WarOfSquirrels.instance.getAdminHandler().get(name);

        if (cubo != null && cubo.isTeleport()) return true;

        player.sendMessage(ChatText.Error("Wrong argument"));

        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String name = context.getArgument(argumentTpName, String.class);
        AdminCubo cubo = WarOfSquirrels.instance.getAdminHandler().get(name);
        Player pl = player.getPlayerEntity();

        cubo.setRespawnPoint(new Vector3(pl.getBlockX(), pl.getBlockY(), pl.getBlockZ()));
        WarOfSquirrels.instance.getAdminHandler().Save();

        return 0;
    }
}
