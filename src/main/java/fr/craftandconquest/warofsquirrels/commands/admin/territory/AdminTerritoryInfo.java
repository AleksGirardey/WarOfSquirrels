package fr.craftandconquest.warofsquirrels.commands.admin.territory;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminTerritoryInfo extends AdminCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("info")
                .then(Commands.argument("territory", StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "territory");

        if (WarOfSquirrels.instance.getTerritoryHandler().get(name) == null) {
            player.sendMessage(ChatText.Error("Territory '" + name + "' does not exist"));
            return false;
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "territory");

        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(name);

        Utils.displayInfoFeather(player.getPlayerEntity(), territory, null, null, null);

        return 0;
    }
}
