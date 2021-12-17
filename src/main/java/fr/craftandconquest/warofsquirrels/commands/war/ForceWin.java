package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.war.AttackTarget;
import fr.craftandconquest.warofsquirrels.object.war.War;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class ForceWin extends AdminCommandBuilder {
    private static final ForceWin CMD = new ForceWin();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("forcewin")
                .then(Commands
                        .argument("cityNameTarget", StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String name = context.getArgument("cityNameTarget", String.class);
        AttackTarget target = WarOfSquirrels.instance.getCityHandler().getCity(name);

//        if (target == null) {
//            target = WarOfSquirrels.instance.getBastionHandler().getBastion(name);
//        }

        War war = WarOfSquirrels.instance.getWarHandler().getWar(target);

        if (war == null) return -1;

        if (war.getCityDefender() == target)
            war.ForceDefenderWin();
        else
            war.ForceAttackerWin();

        return 1;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return super.ErrorMessage();
    }
}
