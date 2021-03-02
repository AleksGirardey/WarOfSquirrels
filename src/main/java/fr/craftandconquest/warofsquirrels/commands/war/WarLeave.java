package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.war.War;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import static net.minecraft.command.Commands.literal;

public class WarLeave extends CityCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return literal("leave").executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        War war = WarOfSquirrels.instance.getWarHandler().getWar(player);

        return war != null && war.contains(player);
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        War war = WarOfSquirrels.instance.getWarHandler().getWar(player);

        if (war.RemovePlayer(player))
            return 1;
        player.getPlayerEntity().sendMessage(new StringTextComponent("You cannot leave the war.")
                .applyTextStyle(TextFormatting.RED).applyTextStyle(TextFormatting.BOLD));

        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return new StringTextComponent("You cannot leave the war.")
                .applyTextStyle(TextFormatting.RED).applyTextStyle(TextFormatting.BOLD);
    }
}
