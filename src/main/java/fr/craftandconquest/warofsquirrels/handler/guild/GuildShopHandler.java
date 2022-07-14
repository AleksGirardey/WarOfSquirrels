package fr.craftandconquest.warofsquirrels.handler.guild;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.UpdatableHandler;
import fr.craftandconquest.warofsquirrels.object.faction.guild.GuildShop;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class GuildShopHandler extends UpdatableHandler<GuildShop> {
    public GuildShopHandler(Logger logger) {
        super("[WoS][GuildShopHandler]", logger);
    }

    @Override
    public void Log() {
        WarOfSquirrels.instance.debugLog(MessageFormat.format("{0} Guild shop generated : {1}", PrefixLogger, dataArray.size()));
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {

    }
}
