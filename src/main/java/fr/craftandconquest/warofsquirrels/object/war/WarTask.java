package fr.craftandconquest.warofsquirrels.object.war;

import java.util.TimerTask;

public class WarTask extends TimerTask {
    private int     beforeMinutes = 59, secondsLeft = 60 * 30;
    private War     war;

    public void     setWar(War war) { this.war = war; }

    @Override
    public void run() {
        beforeMinutes--;
        secondsLeft--;

        war.UpdateCapture();

        if (beforeMinutes == 0) {
            war.AddDefenderPoints(33);
            beforeMinutes = 59;
        }
        if (war.CheckWin() || secondsLeft < 0) {
            this.cancel();
            war.LaunchRollback();
        }
    }
}
