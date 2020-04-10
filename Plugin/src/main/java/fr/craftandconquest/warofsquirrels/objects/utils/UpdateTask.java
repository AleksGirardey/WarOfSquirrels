package fr.craftandconquest.warofsquirrels.objects.utils;

import fr.craftandconquest.warofsquirrels.objects.Core;
import org.spongepowered.api.scheduler.Task;

import java.util.function.Consumer;

public class UpdateTask implements Consumer<Task> {
    @Override
    public void accept(Task task) {
        Core.getUpdateHandler().update();
    }
}
