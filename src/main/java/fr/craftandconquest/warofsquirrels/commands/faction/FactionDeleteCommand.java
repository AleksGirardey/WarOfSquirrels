package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class FactionDeleteCommand extends FactionCommandMayor implements IAdminCommand {
    private final String argumentName = "[factionName]";

    private static final FactionDeleteCommand args = new FactionDeleteCommand(true);

    public FactionDeleteCommand() {
        this(false);
    }

    private FactionDeleteCommand(boolean hasArgs) {
        withArgs = hasArgs;
    }

    private final boolean withArgs;

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands
                .literal("delete")
                .executes(this)
                .then(Commands.argument(argumentName, StringArgumentType.string()).executes(args));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        if (withArgs) return IsAdmin(player);

        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        Faction faction;

        if (withArgs)
            faction = WarOfSquirrels.instance.getFactionHandler().get(context.getArgument(argumentName, String.class));
        else
            faction = player.getCity().getFaction();

        WarOfSquirrels.instance.getFactionHandler().Delete(faction);
        StringTextComponent message = new StringTextComponent("La faction '" + faction.getDisplayName() + "' a été dissoute libérant de son emprise ses territoires et ses villes.");
        message.applyTextStyle(TextFormatting.GOLD);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return new StringTextComponent("Vous ne pouvez pas utiliser cette commande").applyTextStyle(TextFormatting.RED);
    }
}
