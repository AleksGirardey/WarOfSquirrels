package fr.craftandconquest.warofsquirrels.commands.admin.teleports;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminTpLink extends AdminCommandBuilder {
    private final String argumentFirstTpName = "firstTpName";
    private final String argumentSecondTpName = "secondTpName";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("link").then(
                Commands.argument(argumentFirstTpName, StringArgumentType.string()).then(
                        Commands.argument(argumentSecondTpName, StringArgumentType.string())
                                .executes(this)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String firstName = context.getArgument(argumentFirstTpName, String.class);
        String secondName = context.getArgument(argumentSecondTpName, String.class);
        AdminCubo first = WarOfSquirrels.instance.getAdminHandler().get(firstName);
        AdminCubo second = WarOfSquirrels.instance.getAdminHandler().get(secondName);

        if (first != null && first.isTeleport() && second != null && second.isTeleport()) return true;

        player.sendMessage(ChatText.Error("Wrong argument"));

        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String firstName = context.getArgument(argumentFirstTpName, String.class);
        String secondName = context.getArgument(argumentSecondTpName, String.class);
        AdminCubo first = WarOfSquirrels.instance.getAdminHandler().get(firstName);
        AdminCubo second = WarOfSquirrels.instance.getAdminHandler().get(secondName);

        first.setLinkedPortal(second.getUuid());

        WarOfSquirrels.instance.getAdminHandler().Save();

        player.sendMessage(ChatText.Success(firstName + " now linked to " + secondName));

        return 0;
    }
}
