package fr.craftandconquest.warofsquirrels.commands.city;

import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public abstract class CityMayorCommandBuilder extends CityCommandBuilder {
    @Override
    protected ITextComponent ErrorMessage() {
        return new StringTextComponent("You need to be at least mayor of your city to perform this command")
                .applyTextStyle(TextFormatting.RED)
                .applyTextStyle(TextFormatting.BOLD);
    }

    @Override
    protected boolean CanDoIt(Player player) {
        return super.CanDoIt(player) && (player.getCity().getOwner() == player);
    }
}

