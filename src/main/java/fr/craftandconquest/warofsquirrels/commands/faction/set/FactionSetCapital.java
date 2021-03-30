package fr.craftandconquest.warofsquirrels.commands.faction.set;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.extractor.ICityExtractor;
import fr.craftandconquest.warofsquirrels.commands.faction.FactionCommandMayor;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class FactionSetCapital extends FactionCommandMayor implements ICityExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("capital")
                .then(getArgumentRegister()
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        if (IsAdmin(player)) return true;

        City city = getArgument(player, context);

        if (city != null && city.getFaction() != null && city.getFaction() == player.getCity().getFaction())
            return true;

        StringTextComponent message = new StringTextComponent("La ville '" + getRawArgument(context)
                + "' n'existe pas ou ne fait pas partit de votre faction.");

        message.applyTextStyle(TextFormatting.RED);
        player.getPlayerEntity().sendMessage(message);
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        City city = getArgument(player, context);

        WarOfSquirrels.instance.getFactionHandler().SetCapital(player.getCity().getFaction(), city);
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return new StringTextComponent("You can't perform this command");
    }
}
