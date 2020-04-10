package fr.craftandconquest.warofsquirrels.handlers;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.utils.UpdateTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class UpdateHandler {

    private Task currentUpdateTask;

    public UpdateHandler() {
        this.create();
    }

    public void update() {
        Core.sendText(Text.of(TextColors.GOLD, "Un nouveau jour se lève sur ", TextStyles.ITALIC, Core.getWorld().getName(), TextStyles.RESET, TextColors.RESET));
        Core.getTerritoryHandler().update();
        Core.getLoanHandler().update();
        this.create();
    }

    public void create() {
        Core.getLogger().debug("Task created");
        currentUpdateTask = Task.builder().execute(new UpdateTask())
                .delay(delayBeforeMidnight(), TimeUnit.SECONDS)
                .name("Midnight update task")
                .submit(Core.getMain());
    }

    private long    delayBeforeMidnight() {
        return 3600L;
        /*
        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, ZoneId.systemDefault());
        ZonedDateTime zonedNext = zonedNow.withHour(0).withMinute(0).withSecond(0);
        if(zonedNow.compareTo(zonedNext) > 0)
            zonedNext = zonedNext.plusDays(1);
        return Duration.between(zonedNow, zonedNext).getSeconds(); */
    }

    public void    cancelTask() {
        currentUpdateTask.cancel();
    }
}
