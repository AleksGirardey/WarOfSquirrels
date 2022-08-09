package fr.craftandconquest.warofsquirrels.commands.admin.set;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.handler.AdminHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminSetClearInventory extends AdminCommandBuilder {
    private final static String cuboNameArgument = "cubo";
    private final static String clearNameArgument = "clear";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("clearinventory")
                .then(Commands
                        .argument(cuboNameArgument, StringArgumentType.string())
                        .then(Commands
                                .argument(clearNameArgument, BoolArgumentType.bool())
                                .executes(this)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (WarOfSquirrels.instance.getAdminHandler().get(StringArgumentType.getString(context, cuboNameArgument)) != null)
            return true;

        player.sendMessage(ChatText.Error("Cubo doesn't exist"), true);

        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        AdminHandler handler = WarOfSquirrels.instance.getAdminHandler();
        boolean clearValue = BoolArgumentType.getBool(context, clearNameArgument);
        AdminCubo cubo = handler.get(StringArgumentType.getString(context, cuboNameArgument));

        cubo.setClearInventoryOnTp(clearValue);

        player.sendMessage(ChatText.Success("Cubo '" + cubo.getDisplayName() + "' has now Clear Inventory on TP to :" + clearValue));

        handler.Save();

        return 0;
    }
}
