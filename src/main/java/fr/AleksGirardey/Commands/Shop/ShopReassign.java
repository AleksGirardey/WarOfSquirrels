package fr.AleksGirardey.Commands.Shop;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Shop;
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
