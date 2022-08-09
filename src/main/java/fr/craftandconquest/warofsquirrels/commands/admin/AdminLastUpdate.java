package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminLastUpdate extends AdminCommandBuilder{
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("lastupdate").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        player.sendMessage(ChatText.Success("Last update on : " +
                dateFormat.format(new Date(WarOfSquirrels.instance.getConfig().getLastUpdateDate()))));
        return 0;
    }
}
