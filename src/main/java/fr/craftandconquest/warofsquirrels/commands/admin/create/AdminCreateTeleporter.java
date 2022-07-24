package fr.craftandconquest.warofsquirrels.commands.admin.create;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminCreateTeleporter extends AdminCommandBuilder {
    private final String argumentTeleportationName = "Name";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("teleportation")
                .then(Commands
                        .argument(argumentTeleportationName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (WarOfSquirrels.instance.getCuboHandler().getPoints(player) != null)
            return true;

        player.sendMessage(ChatText.Error("Set boundaries first (/cubo)"), true);
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String name = context.getArgument(argumentTeleportationName, String.class);

        if (WarOfSquirrels.instance.getAdminHandler().CreateTeleporter(player, name) != null)
            WarOfSquirrels.instance.getAdminHandler().Save();

        return 0;
    }
}
