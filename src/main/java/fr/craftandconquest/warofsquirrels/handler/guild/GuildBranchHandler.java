package fr.craftandconquest.warofsquirrels.handler.guild;

import fr.craftandconquest.warofsquirrels.handler.Handler;
import fr.craftandconquest.warofsquirrels.object.faction.guild.GuildBranch;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import org.apache.logging.log4j.Logger;

public class GuildBranchHandler extends Handler<GuildBranch> {


    public GuildBranchHandler(Logger logger) {
        super("[WoS][GuildBranchHandler]", logger);
    }

    @Override
    protected boolean add(GuildBranch value) {
        return false;
    }

    @Override
    public boolean Delete(GuildBranch value) {
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
