package fr.craftandconquest.warofsquirrels.handler.broadcast;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.channels.Channel;
import fr.craftandconquest.warofsquirrels.object.channels.CityChannel;
import fr.craftandconquest.warofsquirrels.object.channels.FactionChannel;
import fr.craftandconquest.warofsquirrels.object.channels.WorldChannel;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.war.PartyWar;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BroadCastHandler {

    private final Logger LOGGER;

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

    public void BroadCastWorldAnnounce(ITextComponent message) {
        worldChannel.SendAnnounce(message);
    }

    public boolean AddPlayerToWorldAnnounce(Player player) {
        return worldChannel.addMember(player);
    }

    public boolean RemovePlayerToWorldAnnounce(Player player) {
        return worldChannel.removeMember(player);
    }

    public boolean AddPlayerToTarget(IChannelTarget target, Player player) {
        return channels.get(target).addMember(player);
    }

    public boolean RemovePlayerToTarget(IChannelTarget target, Player player) {
        return channels.get(target).removeMember(player);
    }

    public boolean RemovePlayerFromTargets(Player player) {
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

    public void         partyChannel(PartyWar party, String message) {
        partyChannel(party, message, null);
    }

    public void         partyChannel(PartyWar party, String message, TextFormatting color) {
        TextComponent text = new StringTextComponent(message);
        text.applyTextStyle(color == null ? TextFormatting.YELLOW : color);

        for (Player p : party.toList())
            p.getPlayerEntity().sendMessage(text);
    }

    public void         partyInvitation(Player sender, Player receiver) {
        String partyMessage = receiver.getDisplayName() + " has been invited to your party.";
        TextComponent receiverMessage = new StringTextComponent(sender.getDisplayName()
                + " invited you to join his party. Type /accept or /refuse to respond.");
        receiverMessage.applyTextStyle(TextFormatting.YELLOW);

        partyChannel(WarOfSquirrels.instance.getPartyHandler().getFromPlayer(sender), partyMessage, TextFormatting.YELLOW);
        receiver.getPlayerEntity().sendMessage(receiverMessage);
    }

    public void cityInvitation(Player receiver, Player sender, City city) {
        String invitationMessage = sender.getDisplayName() +
                " invited you to join " +
                city.displayName +
                ". Use /accept or /refuse to respond.";
        String cityMessage = receiver.getDisplayName() + " has been invited to join your city.";
        receiver.getPlayerEntity().sendMessage(new StringTextComponent(invitationMessage));
        BroadCastMessage(sender.getCity(), sender, new StringTextComponent(cityMessage), true);
    }


    //ToDo: Ajouter le message aux assistants de Faction (maire de toutes les villes)
    public void allianceInvitation(Faction factionSender, Faction factionReceiver) {
        Player          factionLeader = factionReceiver.getCapital().getOwner();
        List<Player>    assistants = factionReceiver.getCapital().getAssistants();
        StringTextComponent toSender = new StringTextComponent(factionReceiver.getDisplayName() + " has been invited to be your ally.");
        StringTextComponent toReceiver = new StringTextComponent("The faction " + factionSender.getDisplayName() + " want to be your ally. Use /accept or /refuse to respond.");

        toReceiver.applyTextStyle(TextFormatting.GOLD);
        toSender.applyTextStyle(TextFormatting.GOLD);

        factionLeader.getPlayerEntity().sendMessage(toReceiver);
        for (Player player : assistants) player.getPlayerEntity().sendMessage(toReceiver);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(factionSender, null, toSender, true);
    }
}
