package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.admin.CustomReward;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class RewardHandler extends Handler<CustomReward> {
    public RewardHandler(Logger logger) {
        super("[WoS][RewardHandler]", logger);
    }

    @Override
    protected void InitVariables() {}

    @Override
    protected void CustomLoad(File configFile) throws IOException {
        dataArray = jsonArrayToList(configFile, CustomReward.class);
    }

    @Override
    public void Log() {
        WarOfSquirrels.instance.debugLog(this.PrefixLogger + "Rewards created: " + dataArray.size());
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {}

    @Override
    public boolean add(CustomReward value) {
        return super.add(value);
    }
}
