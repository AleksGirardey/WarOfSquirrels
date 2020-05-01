package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import org.apache.logging.log4j.Logger;

public class InfluenceHandler extends Handler<Influence> {
    protected InfluenceHandler(Logger logger) {
        super("[WoS][InfluenceHandler]", logger);
    }

    @Override
    protected boolean Populate() {
        return false;
    }

    @Override
    public boolean Delete(Influence value) {
        return false;
    }

    @Override
    public void Log() {

    }

    @Override
    public String getConfigDir() {
        return null;
    }

    @Override
    protected String getConfigPath() {
        return null;
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {

    }
}
