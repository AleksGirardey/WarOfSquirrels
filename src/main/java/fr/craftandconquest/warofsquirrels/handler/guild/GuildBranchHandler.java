package fr.craftandconquest.warofsquirrels.handler.guild;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.UpdatableHandler;
import fr.craftandconquest.warofsquirrels.object.faction.guild.GuildBranch;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class GuildBranchHandler extends UpdatableHandler<GuildBranch> {
    public GuildBranchHandler(Logger logger) {
        super("[WoS][GuildBranchHandler]", logger);
    }

    @Override
    public void Log() {
        WarOfSquirrels.instance.debugLog(MessageFormat.format("{0} Guild branch generated : {1}", PrefixLogger, dataArray.size()));
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {

    }
}
