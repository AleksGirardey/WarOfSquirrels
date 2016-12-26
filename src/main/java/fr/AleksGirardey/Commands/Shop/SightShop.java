package fr.AleksGirardey.Commands.Shop;

import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.Optional;

public abstract class       SightShop extends Commands {
    BlockRayHit<World>      hit;

    @Override
    protected boolean   CanDoIt(DBPlayer player) {
        return super.CanDoIt(player) && player.getUser().hasPermission("minecraft.command.op");
    }

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        Optional<Player> p = player.getUser().getPlayer();

        if (p.isPresent()) {
            Player          pl = p.get();
            BlockRay<World> blockRay = BlockRay.from(pl).stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1)).build();

            Optional<BlockRayHit<World>>        hitOpt = blockRay.end();
            if (hitOpt.isPresent()) {
                BlockRayHit<World>              hit = hitOpt.get();

                if (hit.getLocation().getBlockType() == BlockTypes.WALL_SIGN
                        && Core.getShopHandler().get(hit.getBlockPosition()) != null) {
                    this.hit = hit;
                    return true;
                }
            }
        }
        player.sendMessage(Text.of(TextColors.RED, "Not a valid shop sign", TextColors.RESET));
        return false;
    }
}
