package fr.AleksGirardey.Objects.WarTask;

import fr.AleksGirardey.Objects.War;
import org.spongepowered.api.scheduler.Task;

import java.util.function.Consumer;

public class        WarTask implements Consumer<Task> {
    private int     beforeMinutes = 60, secondesLeft = 60 * 30;
    private War     war;

    public void     setWar(War war) { this.war = war; }

    @Override
    public void accept(Task task) {
        beforeMinutes--;
        secondesLeft--;

        if (beforeMinutes == 0) {
            war.addDefenderPoints(33);
            beforeMinutes = 60;
        }
        if (war.checkWin() || secondesLeft < 0) {
            task.cancel();
            war.lauchRollback();
        }
    }
}
