package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CityLeave extends CityCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("leave").executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return player.getCity().getOwner() != player;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        player.getCity().removeCitizen(player, false);
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return new StringTextComponent("Vous ne pouvez pas quitter votre ville.").applyTextStyle(TextFormatting.RED);
    }
}
