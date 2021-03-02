package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.war.AttackTarget;
import fr.craftandconquest.warofsquirrels.object.war.War;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class WarInfo extends CommandBuilder {
    private final WarInfo CMD_NO_ARGS = new WarInfo(false);
    private final WarInfo CMD_ARGS = new WarInfo(true);

    private final boolean args;
    private String targetName;

    public WarInfo(){ args = false; }

    private WarInfo(boolean hasArgs){
        args = hasArgs;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("info")
                .executes(CMD_NO_ARGS)
                .then(Commands
                        .argument("cityTargetName", StringArgumentType.string())
                        .executes(CMD_ARGS));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        War war;
        String targetName;

        if (args) {
            targetName = context.getArgument("cityTargetName", String.class);
            AttackTarget target = WarOfSquirrels.instance.getCityHandler().getCity(targetName);

            if (target == null) {
    //            target = WarOfSquirrels.instance.getBastionHandler().getBastion(targetName);
                if (target == null) return false;
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
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        String targetName = context.getArgument("cityTargetName", String.class);
        AttackTarget target = WarOfSquirrels.instance.getCityHandler().getCity(targetName);

//        if (target == null)
//            target = WarOfSquirrels.instance.getBastionHandler().getBastion(targetName);

        War war = WarOfSquirrels.instance.getWarHandler().getWar(target);

        war.displayInfo(player);

        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return new StringTextComponent( "The target '" + targetName + "' is not participating to a war")
                .applyTextStyle(TextFormatting.RED)
                .applyTextStyle(TextFormatting.BOLD);
    }
}
