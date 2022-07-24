package fr.craftandconquest.warofsquirrels.handler.guild;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.UpdatableHandler;
import fr.craftandconquest.warofsquirrels.object.faction.guild.GuildBranch;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

public class GuildBranchHandler extends UpdatableHandler<GuildBranch> {
    public GuildBranchHandler(Logger logger) {
        super("[WoS][GuildBranchHandler]", logger);
    }

    @Override
    protected void InitVariables() { }

    @Override
    protected void CustomLoad(File configFile) throws IOException {
        dataArray = jsonArrayToList(configFile, GuildBranch.class);
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Guild branch generated : {1}", PrefixLogger, dataArray.size()));
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {

    }
}
