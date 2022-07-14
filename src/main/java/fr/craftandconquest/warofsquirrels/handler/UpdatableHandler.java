package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.object.IUpdate;
import fr.craftandconquest.warofsquirrels.object.RegistryObject;
import org.apache.logging.log4j.Logger;

public abstract class UpdatableHandler<U extends RegistryObject & IUpdate> extends Handler<U> implements IUpdate {
    protected UpdatableHandler(String prefix, Logger logger) {
        super(prefix, logger);
    }

    protected UpdatableHandler() { super("", null); }

    public void update() {
        dataArray.forEach(IUpdate::update);
    }
}
