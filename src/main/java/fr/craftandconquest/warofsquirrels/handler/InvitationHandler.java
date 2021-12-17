package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.invitation.Invitation;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class InvitationHandler /*extends Handler<Invitation>*/ {
    public InvitationHandler(Logger logger) {
        //super("[WoS][InvitationHandler]", logger);
    }

    private final List<Invitation> dataArray = new ArrayList<>();

    public boolean CreateInvitation(Invitation invitation) {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DeleteTask(invitation);
            }
        }, WarOfSquirrels.instance.getConfig().getInvitationTime() * 1000L);

        invitation.setTask(timer);
        return add(invitation);
    }

    public void DeleteTask(Invitation invitation) {
        invitation.cancel();
        Delete(invitation);
    }

    public boolean HandleInvitation(FullPlayer player, boolean isAccepted) {
        Invitation invitation = null;

        for (Invitation inv : dataArray) {
            if (inv.concern(player))
                invitation = inv;
        }

        if (invitation != null) {
            if (isAccepted) invitation.accept();
            else invitation.refuse();

            invitation.getTask().cancel();
            Delete(invitation);
            return true;
        }
        return false;
    }

    //    @Override
    protected boolean add(Invitation value) {
        dataArray.add(value);

        return true;
    }

    //    @Override
    public boolean Delete(Invitation value) {
        if (!dataArray.contains(value)) return false;
        dataArray.remove(value);

        return true;
    }
}
