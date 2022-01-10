package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class BastionHandler extends Handler<Bastion> {
    public BastionHandler(Logger logger) {
        super("[WoS][BastionHandler]", logger);
    }

    public Bastion get(UUID uuid) {
        for (Bastion bastion : dataArray) {
            if (bastion.getBastionUuid().equals(uuid))
                return bastion;
        }
        return null;
    }

    @Override
    protected boolean add(Bastion value) {
        return false;
    }

    @Override
    public boolean Delete(Bastion value) {
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
