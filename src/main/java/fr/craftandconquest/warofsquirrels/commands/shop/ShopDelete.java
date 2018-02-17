package fr.craftandconquest.commands.shop;

import fr.craftandconquest.objects.Core;
import fr.craftandconquest.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class ShopDelete extends SightShop {
    @Override
    protected   CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Core.getShopHandler().delete(this.hit.getBlockPosition());
        return CommandResult.success();
    }
}
