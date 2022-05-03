package fr.craftandconquest.warofsquirrels.commands.admin.teleports;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class AdminTpList extends AdminCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("list").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        List<AdminCubo> list = WarOfSquirrels.instance.getAdminHandler().getAllTp();
        MutableComponent message = ChatText.Success("");

        message.append("== Admin Tp list ==\n");

        for (AdminCubo adminCubo : list) {
            message.append(" - " + adminCubo.getName() + "\n");
        }

        player.sendMessage(message);

        return 0;
    }
}
