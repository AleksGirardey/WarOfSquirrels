package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

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

        }

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce();
        return 0;
    }
}
