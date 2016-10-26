package fr.AleksGirardey.Objects.Events;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.World;

import java.util.List;

public class WarBreakEvent implements ChangeBlockEvent.Break, Cancellable {
    @Override
    public List<Transaction<BlockSnapshot>> getTransactions() {
        return null;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {

    }

    @Override
    public World getTargetWorld() {
        return null;
    }

    @Override
    public Cause getCause() {
        return null;
    }
}
