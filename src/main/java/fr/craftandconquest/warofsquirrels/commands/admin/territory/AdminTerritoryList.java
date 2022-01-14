package fr.craftandconquest.warofsquirrels.commands.admin.territory;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class AdminTerritoryList extends AdminCommandBuilder {
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
        MutableComponent message = ChatText.Success("== Territories ==\n");

        List<Territory> list = WarOfSquirrels.instance.getTerritoryHandler().getAll();

        list.forEach(t -> message.append("  ["+ t.getPosX() + ";" + t.getPosZ() + "] " + t.getName()).append("\n"));

        player.sendMessage(message);

        return 0;
    }
}
