package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class WarPeaceTime extends AdminCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands
                .literal("peace")
                .executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        boolean peace = !WarOfSquirrels.instance.config.getConfiguration().isPeaceTime();
        StringTextComponent message = new StringTextComponent("");
        WarOfSquirrels.instance.config.getConfiguration().setPeaceTime(peace);

        if (peace) {
            message.appendText("Ho no.. Seems like peace have been declared").applyTextStyle(TextFormatting.GREEN);
        } else
            message.appendText("MOUHAHAHAH, TIME TO FIGHT !").applyTextStyle(TextFormatting.DARK_GREEN);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
        return 0;
    }
}
