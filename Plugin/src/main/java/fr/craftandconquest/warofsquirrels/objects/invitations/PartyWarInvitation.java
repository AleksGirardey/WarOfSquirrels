package fr.craftandconquest.warofsquirrels.objects.invitations;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.war.PartyWar;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class        PartyWarInvitation extends Invitation{
    private PartyWar        _party;

    public          PartyWarInvitation(DBPlayer player, DBPlayer sender, PartyWar party) {
        super(player, sender, Reason.PartyWar);
        this._party = party;
    }

    @Override
    public void accept() {
        _party.addPlayer(_player);
        Core.getBroadcastHandler().partyChannel(_party, _player.getDisplayName() + " join the party");
    }

    @Override
    public void refuse() {
        _party.SendMessage(Text.of(TextColors.RED, _player.getDisplayName() + " refuse to join the party", TextColors.RESET));
        _player.sendMessage(Text.of(TextColors.RED, "The invitation from " + _sender.getDisplayName() + " have been refused.", TextColors.RESET));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!Invitation.class.isAssignableFrom(obj.getClass()))
            return false;
        final Invitation inv = (Invitation) obj;
        return (this._player.equals(inv._player) &&
                this._sender.equals(inv._sender) &&
                this._reason.equals(inv._reason));
    }

    public PartyWar     getParty() { return _party; }
}