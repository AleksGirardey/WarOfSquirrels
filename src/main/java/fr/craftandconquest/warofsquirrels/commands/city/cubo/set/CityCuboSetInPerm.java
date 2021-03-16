package fr.craftandconquest.warofsquirrels.commands.city.cubo.set;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class CityCuboSetInPerm extends CommandBuilder implements IAdminCommand {
    private final String cuboNameArgument = "[CuboName]";
    private final String buildArgument = "[Build]";
    private final String containerArgument = "[Container]";
    private final String switchArgument = "[Switch]";
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("inperm").then(Commands
                .argument(cuboNameArgument, StringArgumentType.string())
                .then(Commands.argument(buildArgument, BoolArgumentType.bool())
                        .then(Commands.argument(containerArgument, BoolArgumentType.bool())
                                .then(Commands.argument(switchArgument, BoolArgumentType.bool())
                                        .executes(this)))));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(context.getArgument(cuboNameArgument, String.class));

        if (cubo == null) return false;

        List<Player> list = new ArrayList<>();

        list.add(cubo.getOwner());
        list.add(cubo.getCity().getOwner());
        list.addAll(cubo.getCity().getAssistants());

        if (list.contains(player))
            return true;

        player.getPlayerEntity()
                .sendMessage(new StringTextComponent("Vous ne pouvez pas modifier les permissions de ce cubo.").applyTextStyle(TextFormatting.RED));
        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {

        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }
}
