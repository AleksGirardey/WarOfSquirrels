package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.war.AttackTarget;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class WarInfo extends CommandBuilder {
    private static final WarInfo CMD_NO_ARGS = new WarInfo(false);
    private static final WarInfo CMD_ARGS = new WarInfo(true);

    private final boolean args;
    private String targetName;

    public WarInfo() {
        args = false;
    }

    private WarInfo(boolean hasArgs) {
        args = hasArgs;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("info")
                .executes(CMD_NO_ARGS)
                .then(Commands
                        .argument("cityTargetName", StringArgumentType.string())
                        .executes(CMD_ARGS));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        War war;
        String targetName;

        if (args) {
            targetName = context.getArgument("cityTargetName", String.class);
            AttackTarget target = WarOfSquirrels.instance.getCityHandler().getCity(targetName);

            if (target == null) {
                //            target = WarOfSquirrels.instance.getBastionHandler().getBastion(targetName);
//                if (target == null)
                return false;
            }
            war = WarOfSquirrels.instance.getWarHandler().getWar(target);
        } else {
            war = WarOfSquirrels.instance.getWarHandler().getWar(player);
            targetName = player.getDisplayName();
        }

        this.targetName = targetName;
        return war != null;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        War war;

        if (args) {
            String targetName = context.getArgument("cityTargetName", String.class);
            AttackTarget target = WarOfSquirrels.instance.getCityHandler().getCity(targetName);

            war = WarOfSquirrels.instance.getWarHandler().getWar(target);
        } else {
            war = WarOfSquirrels.instance.getWarHandler().getWar(player);
        }

        war.displayInfo(player);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("The target '" + targetName + "' is not participating to a war")
                .withStyle(ChatFormatting.BOLD);
    }
}
