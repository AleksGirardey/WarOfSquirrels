package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminForceDailyUpdate extends AdminCommandBuilder{
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("forcedailyupdate").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        WarOfSquirrels.instance.getUpdateHandler().DailyUpdate();

        return 0;
    }
}
