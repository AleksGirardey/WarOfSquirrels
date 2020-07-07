package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.invitation.Invitation;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class InvitationHandler extends Handler<Invitation> {
    public InvitationHandler(Logger logger) {
        super("[WoS][InvitationHandler]", logger);
    }

    public boolean CreateInvitation(Invitation invitation) {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DeleteTask(invitation);
            }
        }, WarOfSquirrels.instance.getConfig().getInvitationTime() * 1000);

        invitation.setTask(timer);
        return add(invitation);
    }

    public void DeleteTask(Invitation invitation) {
        invitation.cancel();
        Delete(invitation);
    }

    public boolean HandleInvitation(Player player, boolean isAccepted) {
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

    @Override
    protected boolean add(Invitation value) {
        dataArray.add(value);

        return true;
    }

    @Override
    public boolean Delete(Invitation value) {
        if (!dataArray.contains(value)) return false;
        dataArray.remove(value);

        return true;
    }

    @Override
    public void Log() {
        // Nothing to log
    }

    @Override
    public void Save() {
        // No need to save
    }

    @Override
    public String getConfigDir() {
        return "";
    }

    @Override
    protected String getConfigPath() {
        return "";
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {
        // Nothing to do
    }
}
