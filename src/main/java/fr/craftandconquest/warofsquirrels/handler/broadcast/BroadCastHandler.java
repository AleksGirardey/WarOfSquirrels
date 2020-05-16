package fr.craftandconquest.warofsquirrels.handler.broadcast;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.channels.Channel;
import fr.craftandconquest.warofsquirrels.object.channels.CityChannel;
import fr.craftandconquest.warofsquirrels.object.channels.FactionChannel;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BroadCastHandler {

    private final Logger LOGGER;

    private final Map<IChannelTarget, Channel> channels = new HashMap<>();

    public BroadCastHandler(Logger logger) {
        LOGGER = logger;
        List<Faction> factionList = WarOfSquirrels.instance.getFactionHandler().getAll();
        List<City> cityList = WarOfSquirrels.instance.getCityHandler().getAll();

        for (Faction faction : factionList) channels.put(faction, new FactionChannel(faction));
        for (City city : cityList) channels.put(city, new CityChannel(city));

        LOGGER.info(String.format("[WoS][BroadCastHandler] %d channels created", channels.size()));
    }

    public void BroadCastMessage(IChannelTarget target, Player sender, ITextComponent message, boolean isAnnounce) {
        if (channels.containsKey(target)) {
            if (isAnnounce) channels.get(target).SendAnnounce(message);
            else if (sender != null) channels.get(target).SendMessage(sender, message);
            else
                LOGGER.warn("[WoS][BroadCastHandler] Couldn't send message :" +
                        "\n\tTarget : {}" +
                        "\n\tSender : NULL" +
                        "\n\tMessage : {}" +
                        "\n\tAnnounce : false", target, message);
        }
    }

    public boolean AddPlayerToTarget(IChannelTarget target, Player player) {
        return channels.get(target).addMember(player);
    }

    public boolean RemovePlayerToTarget(IChannelTarget target, Player player) {
        return channels.get(target).removeMember(player);
    }

    public boolean AddTarget(IChannelTarget target, Channel channel) {
        return channels.putIfAbsent(target, channel) == null;
    }

    public boolean DeleteTarget(IChannelTarget target) {
        return channels.remove(target) != null;
    }
}
