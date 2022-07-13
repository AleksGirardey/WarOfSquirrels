package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public class CityChannel extends Channel {

    private final City city;

    public CityChannel(City city) {
        super(city.getBroadCastTarget());

        this.city = city;
    }

    @Override
    public MutableComponent transformText(FullPlayer sender, MutableComponent text) {
        return ChatText.Colored(String.format("[%s][%s] ",
                        sender.getCity().getDisplayName(), Utils.getDisplayNameWithRank(sender)), ChatFormatting.DARK_AQUA)
                .append(text);
    }

    @Override
    protected MutableComponent transformTextAnnounce(MutableComponent text) {
        return ChatText.Colored(String.format("[%s] ", city.getDisplayName()), ChatFormatting.GOLD)
                .append(text);
    }
}
