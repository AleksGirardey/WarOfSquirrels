package fr.craftandconquest.warofsquirrels.handler.broadcast;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.channels.Channel;
import fr.craftandconquest.warofsquirrels.object.channels.CityChannel;
import fr.craftandconquest.warofsquirrels.object.channels.FactionChannel;
import fr.craftandconquest.warofsquirrels.object.channels.WorldChannel;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BroadCastHandler {

    private final Logger LOGGER;

    @Getter
    private final WorldChannel worldChannel = new WorldChannel();
//    private final SupportChannel supportChannel;

    private final Map<IChannelTarget, Channel> channels = new HashMap<>();

    public BroadCastHandler(Logger logger) {
        LOGGER = logger;
        List<Faction> factionList = WarOfSquirrels.instance.getFactionHandler().getAll();
        List<City> cityList = WarOfSquirrels.instance.getCityHandler().getAll();

        for (Faction faction : factionList) channels.put(faction, new FactionChannel(faction));
        for (City city : cityList) channels.put(city, new CityChannel(city));

        LOGGER.info(String.format("[WoS][BroadCastHandler] %d channels created", channels.size()));
    }

    public void BroadCastMessage(IChannelTarget target, FullPlayer sender, MutableComponent message, boolean isAnnounce) {
        if (channels.containsKey(target)) {
            if (isAnnounce) channels.get(target).SendAnnounce(message);
            else if (sender != null) channels.get(target).SendMessage(sender, message);
            else LOGGER.warn("""
                        [WoS][BroadCastHandler] Couldn't send message :
                        \tTarget : {}
                        \tSender : NULL
                        \tMessage : {}
                        \tAnnounce : false""", target, message);
        }
    }

    public void BroadCastWorldAnnounce(MutableComponent message) {
        LOGGER.info("[WoS][BroadcastHandler] World Announce : " + message.getString());
        worldChannel.SendAnnounce(message);
    }

    public boolean AddPlayerToWorldAnnounce(FullPlayer player) {
        return worldChannel.addMember(player);
    }

    public boolean RemovePlayerToWorldAnnounce(FullPlayer player) {
        return worldChannel.removeMember(player);
    }

    public boolean AddPlayerToTarget(IChannelTarget target, FullPlayer player) {
        return channels.get(target).addMember(player);
    }

    public boolean RemovePlayerFromTarget(IChannelTarget target, FullPlayer player) {
        return channels.get(target).removeMember(player);
    }

    public boolean RemovePlayerFromTargets(FullPlayer player) {
        channels.forEach(((target, channel) -> channel.removeMember(player)));
        return true;
    }

    public boolean AddTarget(IChannelTarget target, Channel channel) {
        return channels.putIfAbsent(target, channel) == null;
    }

    public boolean DeleteTarget(IChannelTarget target) {
        if (!channels.get(target).clearMembers())
            return false;
        return channels.remove(target) != null;
    }

    public void partyChannel(Party party, String message) {
        partyChannel(party, message, null);
    }

    public void partyChannel(Party party, String message, ChatFormatting color) {
        MutableComponent text = ChatText.Colored(message, color == null ? ChatFormatting.YELLOW : color);

        for (FullPlayer p : party.toList())
            p.sendMessage(text);
    }

    public void partyInvitation(FullPlayer sender, FullPlayer receiver) {
        String partyMessage = receiver.getDisplayName() + " has been invited to your party.";
        MutableComponent receiverMessage = ChatText.Colored(sender.getDisplayName()
                + " invited you to join his party. Type /accept or /refuse to respond.", ChatFormatting.YELLOW);

        partyChannel(WarOfSquirrels.instance.getPartyHandler().getFromPlayer(sender), partyMessage, ChatFormatting.YELLOW);
        receiver.sendMessage(receiverMessage);
    }

    public void cityInvitation(FullPlayer receiver, FullPlayer sender, City city) {
        String invitationMessage = sender.getDisplayName() +
                " invited you to join " +
                city.getDisplayName() +
                ". Use /accept or /refuse to respond.";
        String cityMessage = receiver.getDisplayName() + " has been invited to join your city.";
        receiver.sendMessage(MutableComponent.create(ComponentContents.EMPTY).append(invitationMessage));
        BroadCastMessage(sender.getCity(), sender, MutableComponent.create(ComponentContents.EMPTY).append(cityMessage), true);
    }


    //ToDo: Ajouter le message aux assistants de Faction (maire de toutes les villes)
    public void allianceInvitation(Faction factionSender, Faction factionReceiver) {
        FullPlayer factionLeader = factionReceiver.getCapital().getOwner();
        List<FullPlayer> assistants = factionReceiver.getCapital().getAssistants();
        MutableComponent toSender = ChatText.Colored(factionReceiver.getDisplayName() + " has been invited to be your ally.", ChatFormatting.GOLD);
        MutableComponent toReceiver = ChatText.Colored("The faction " + factionSender.getDisplayName() + " want to be your ally. Use /accept or /refuse to respond.", ChatFormatting.GOLD);

        factionLeader.sendMessage(toReceiver);
        for (FullPlayer player : assistants) player.sendMessage(toReceiver);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(factionSender, null, toSender, true);
    }

    public void WarAnnounce(War war, War.WarState state) {
        MutableComponent text = MutableComponent.create(ComponentContents.EMPTY);

        if (state == War.WarState.Preparation)
            text.append(war.getCityAttacker().getDisplayName() + " attacks " + war.getCityDefender() + " prepare yourself for the fight. You have 2 minutes before the hostilities starts !");
        else if (state == War.WarState.War)
            text.append("Let the battle... BEGIN !");
        else
            text.append("Caren you have 1 min to evacuate the city before the place got rollback");
        BroadCastMessage(war, null, text, true);
    }
}
