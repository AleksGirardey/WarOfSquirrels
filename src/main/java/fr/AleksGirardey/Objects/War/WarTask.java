package fr.AleksGirardey.Objects.War;

import org.spongepowered.api.scheduler.Task;

import java.util.function.Consumer;

public class        WarTask implements Consumer<Task> {
    private int     beforeMinutes = 59, secondsLeft = 60 * 30;
    private War     war;

    public void     setWar(War war) { this.war = war; }

    @Override
    public void     accept(Task task) {
        beforeMinutes--;
        secondsLeft--;

        war.updateCapture();

        if (beforeMinutes == 0) {
            war.addDefenderPoints(33);
            beforeMinutes = 59;
        }
        if (war.checkWin() || secondsLeft < 0) {
            task.cancel();
            war.lauchRollback();
        }
    }
}
