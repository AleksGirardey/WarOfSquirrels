package fr.craftandconquest.warofsquirrels.object.invitation;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;

public class CityInvitation extends Invitation {
    @Getter
    private final City city;

    public CityInvitation(FullPlayer receiver, FullPlayer sender, City city) {
        super(receiver, sender, InvitationType.City);
        this.city = city;
        WarOfSquirrels.instance.getBroadCastHandler().cityInvitation(receiver, sender, city);
    }

    @Override
    public void accept() {
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(city, receiver);
        WarOfSquirrels.instance.getCityHandler().NewCitizen(receiver, city);
    }

    @Override
    public void refuse() {
        MutableComponent toSender = ChatText.Error(receiver.getDisplayName() + " declined your invitation.");
        MutableComponent toReceiver = ChatText.Error("The invitation from " + sender.getDisplayName() + " have been decline.");

        sender.getPlayerEntity().sendMessage(toSender, Util.NIL_UUID);
        receiver.getPlayerEntity().sendMessage(toReceiver, Util.NIL_UUID);
    }

    @Override
    public void cancel() {
        MutableComponent toSender = ChatText.Error("The invitation sent to '"
                + receiver.getDisplayName() + "' has expired.");
        MutableComponent toReceiver = ChatText.Error("The invitation from '"
                + sender.getDisplayName() + "' has expired.");

        sender.getPlayerEntity().sendMessage(toSender, Util.NIL_UUID);
        receiver.getPlayerEntity().sendMessage(toReceiver, Util.NIL_UUID);
    }
}
