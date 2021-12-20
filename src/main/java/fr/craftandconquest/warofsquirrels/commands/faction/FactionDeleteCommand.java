package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

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
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("delete")
                .executes(this)
                .then(Commands.argument(argumentName, StringArgumentType.string()).executes(args));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (withArgs) return IsAdmin(player);

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Faction faction;

        if (withArgs)
            faction = WarOfSquirrels.instance.getFactionHandler().get(context.getArgument(argumentName, String.class));
        else
            faction = player.getCity().getFaction();

        WarOfSquirrels.instance.getFactionHandler().Delete(faction);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(ChatText.Colored(
                "La faction '" + faction.getDisplayName() + "' a été dissoute libérant de son emprise ses territoires et ses villes.",
                ChatFormatting.GOLD));
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("Vous ne pouvez pas utiliser cette commande");
    }
}
