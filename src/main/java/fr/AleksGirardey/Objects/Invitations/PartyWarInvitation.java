package fr.AleksGirardey.Objects.Invitations;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.War.PartyWar;
import org.spongepowered.api.entity.living.player.Player;

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
        _party.Send(_player.getDisplayName() + " refuse to join the party");
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