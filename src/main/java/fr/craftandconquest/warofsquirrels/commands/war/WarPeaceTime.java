package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class WarPeaceTime extends AdminCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("peace")
                .executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        boolean peace = !WarOfSquirrels.instance.config.getConfiguration().isPeaceTime();
        MutableComponent message;
        WarOfSquirrels.instance.config.getConfiguration().setPeaceTime(peace);

        if (peace) {
            message = ChatText.Success("Ho no.. Seems like peace have been declared");
        } else
            message = ChatText.Success("MOUHAHAHAH, TIME TO FIGHT !");

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
        return 0;
    }
}
