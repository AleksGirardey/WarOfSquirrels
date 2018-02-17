package fr.craftandconquest.warofsquirrels.commands.shop;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Shop;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class ShopReassign extends SightShop {
    @Override
    protected boolean       SpecialCheck(DBPlayer player, CommandContext context) { return true; }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Shop shop = Core.getShopHandler().get(this.hit.getBlockPosition());

        shop.setPlayer(context.<DBPlayer>getOne("[player]").orElse(null));
        shop.actualize();
        return CommandResult.success();
    }
}
