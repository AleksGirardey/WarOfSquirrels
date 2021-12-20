package fr.craftandconquest.warofsquirrels.object.invitation;

import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import lombok.Getter;
import lombok.Setter;

import java.util.Timer;

public abstract class Invitation {
    public enum InvitationType {
        Faction,
        City,
        Alliance,
        PartyWar
    }

    @Getter
    @Setter
    protected FullPlayer receiver;
    @Getter
    @Setter
    protected FullPlayer sender;
    @Getter
    @Setter
    protected Faction factionReceiver;
    @Getter
    @Setter
    protected Timer task;
    @Getter
    @Setter
    protected boolean executed;
    @Getter
    @Setter
    protected InvitationType invitationType;

    public Invitation(FullPlayer receiver, FullPlayer sender, InvitationType type) {
        this.receiver = receiver;
        this.sender = sender;
        invitationType = type;
    }

    public Invitation(FullPlayer sender, InvitationType type, Faction faction) {
        this.sender = sender;
        this.invitationType = type;
        this.factionReceiver = faction;
    }

    public abstract void accept();

    public abstract void refuse();

    public abstract void cancel();

    public boolean concern(FullPlayer player) {
        return receiver == player;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!Invitation.class.isAssignableFrom(obj.getClass()))
            return false;
        final Invitation inv = (Invitation) obj;
        return (this.receiver.equals(inv.receiver) &&
                this.sender.equals(inv.sender) &&
                this.invitationType.equals(inv.invitationType));
    }
}
