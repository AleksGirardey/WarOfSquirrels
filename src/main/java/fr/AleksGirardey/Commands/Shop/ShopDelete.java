package fr.AleksGirardey.Commands.Shop;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class ShopDelete extends SightShop {
    @Override
    protected   CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Core.getShopHandler().delete(this.hit.getBlockPosition());
        return CommandResult.success();
    }
}
