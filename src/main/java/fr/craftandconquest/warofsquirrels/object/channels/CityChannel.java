package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CityChannel extends Channel {

    private final City city;

    public CityChannel(City city) {
        super(city.getBroadCastTarget());

        this.city = city;
    }

    @Override
    public ITextComponent transformText(Player sender, ITextComponent text) {
        return new StringTextComponent(String.format("[%s][%s] ",
                sender.getCity().displayName, Utils.getDisplayNameWithRank(sender)))
                .applyTextStyle(TextFormatting.DARK_AQUA);
    }

    @Override
    protected ITextComponent transformTextAnnounce(ITextComponent text) {
        return new StringTextComponent(String.format("[%s] %s", city.displayName, text))
                .applyTextStyle(TextFormatting.GOLD);
    }
}
