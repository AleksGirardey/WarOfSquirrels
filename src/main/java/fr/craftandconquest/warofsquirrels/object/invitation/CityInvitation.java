package fr.craftandconquest.warofsquirrels.object.invitation;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import lombok.Getter;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CityInvitation extends Invitation {
    @Getter private final City city;

    public CityInvitation(Player receiver, Player sender, City city) {
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
        StringTextComponent toSender = new StringTextComponent(receiver.getDisplayName() + " declined your invitation.");
        StringTextComponent toReceiver = new StringTextComponent("The invitation from " + sender.getDisplayName() + " have been decline.");

        toSender.applyTextStyle(TextFormatting.RED);
        toReceiver.applyTextStyle(TextFormatting.RED);
        sender.getPlayerEntity().sendMessage(toSender);
        receiver.getPlayerEntity().sendMessage(toReceiver);
    }
}
