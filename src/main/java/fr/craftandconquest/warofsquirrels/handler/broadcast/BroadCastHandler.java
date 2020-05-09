package fr.craftandconquest.warofsquirrels.handler.broadcast;

import fr.craftandconquest.warofsquirrels.object.channels.CityChannel;
import fr.craftandconquest.warofsquirrels.object.channels.FactionChannel;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import net.minecraft.util.text.ITextComponent;

import java.util.Map;

public class BroadCastHandler {

    private Map<Faction, FactionChannel> factionChannels;
    private Map<City, CityChannel> cityChannels;


    public void BroadCastMessage(BroadCastTarget target, Object ... objects) {
        switch (target) {
            case CITY:
                BroadCastToCity((City) objects[0], (ITextComponent) objects[1], (boolean) objects[2]);
            case FACTION:
                BroadCastToFaction((Faction) objects[0], (ITextComponent) objects[1],  (boolean) objects[2]);
            default:
                // Nothing to do
        }
    }

    private void BroadCastToCity(City city, ITextComponent message, boolean isAnnounce) {
        if (cityChannels.containsKey(city)) {
            if (isAnnounce) cityChannels.get(city).SendAnnounce(message);
            else cityChannels.get(city).SendMessage();
        }
    }

    private void BroadCastToFaction(Faction faction, ITextComponent message, boolean isAnnounce) {

    }
}
