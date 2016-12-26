package fr.AleksGirardey.Listeners;

import com.flowpowered.math.vector.Vector3i;
import fr.AleksGirardey.Handlers.ShopHandler;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Cubo;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Permissions;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockListener {

    private static List<BlockType>  ContainersBlock;
    private static List<BlockType>  SwitchableBlocks;

    static {
        SwitchableBlocks = new ArrayList<>();
        SwitchableBlocks.add(BlockTypes.ACACIA_DOOR);
        SwitchableBlocks.add(BlockTypes.ACACIA_FENCE_GATE);
        SwitchableBlocks.add(BlockTypes.BIRCH_DOOR);
        SwitchableBlocks.add(BlockTypes.BIRCH_FENCE_GATE);
        SwitchableBlocks.add(BlockTypes.DARK_OAK_DOOR);
        SwitchableBlocks.add(BlockTypes.DARK_OAK_FENCE_GATE);
        SwitchableBlocks.add(BlockTypes.JUNGLE_DOOR);
        SwitchableBlocks.add(BlockTypes.JUNGLE_FENCE_GATE);
        SwitchableBlocks.add(BlockTypes.SPRUCE_DOOR);
        SwitchableBlocks.add(BlockTypes.SPRUCE_FENCE_GATE);
        SwitchableBlocks.add(BlockTypes.WOODEN_DOOR);
        SwitchableBlocks.add(BlockTypes.FENCE_GATE);
        SwitchableBlocks.add(BlockTypes.STONE_BUTTON);
        SwitchableBlocks.add(BlockTypes.WOODEN_BUTTON);
        SwitchableBlocks.add(BlockTypes.LEVER);

        ContainersBlock = new ArrayList<>();
        ContainersBlock.add(BlockTypes.CHEST);
        ContainersBlock.add(BlockTypes.ENDER_CHEST);
        ContainersBlock.add(BlockTypes.FURNACE);
        ContainersBlock.add(BlockTypes.ANVIL);
        ContainersBlock.add(BlockTypes.BREWING_STAND);
        ContainersBlock.add(BlockTypes.DISPENSER);
        ContainersBlock.add(BlockTypes.LIT_FURNACE);
        ContainersBlock.add(BlockTypes.HOPPER);
    }

    @Listener(order = Order.FIRST)
    public void         onBlockDestroy(ChangeBlockEvent.Break event) {
        final Player    player = event.getCause().first(Player.class).orElse(null);
        Text            message = Text.builder("You can't destroy this").color(TextColors.RED).build();

        if (event.getCause().root() instanceof BlockSnapshot && ((BlockSnapshot) event.getCause().root()).getState().getType() == BlockTypes.FIRE) {
            Core.Send("Fire break");
            event.setCancelled(true);
        }

        event.getTransactions().stream().filter(transaction -> transaction.getOriginal().getState().getType() == BlockTypes.WALL_SIGN)
                .forEach(transaction -> {
                    if (Core.getShopHandler().get(transaction.getOriginal().getPosition()) != null
                            && !Core.getWarHandler().isConcerned(transaction.getOriginal().getPosition()))
                        Core.getShopHandler().delete(transaction.getOriginal().getPosition());
                });

        if (player != null) {
            if (!checkCuboPerms(player, event)) {
                player.sendMessage(message);
                event.setCancelled(true);
            } else if (!checkChunkPerms(player, event)) {
                player.sendMessage(message);
                event.setCancelled(true);
            }
        }
    }

    @Listener(order = Order.FIRST)
    public void         onBlockPlaced(ChangeBlockEvent.Place event) {
        final Player    player = event.getCause().first(Player.class).orElse(null);
        Text            message = Text.builder("You can't build here").color(TextColors.RED).build();

        if (player != null) {
            if (!checkChunkPerms(player, event)) {
                player.sendMessage(message);
                event.setCancelled(true);
            } else if (!checkCuboPerms(player, event)) {
                player.sendMessage(message);
                event.setCancelled(true);
            }
        }
    }

    @Listener(order = Order.FIRST)
    public void             onBlockInteract(InteractBlockEvent.Secondary event) {
        DBPlayer            player = Core.getPlayerHandler().get((Player) event.getCause().getNamedCauses().get("Source"));
        Location<World>     location = event.getTargetBlock().getLocation().orElse(null);
        int                 x, z;

        if (location != null) {
            x = location.getBlockX();
            z = location.getBlockZ();
            if (Core.getChunkHandler().exists(x / 16, z / 16)) {
                if (SwitchableBlocks.contains(event.getTargetBlock().getState().getType()) &&
                        !Core.getPermissionHandler().ableTo(player, Core.getChunkHandler().get(x / 16, z / 16), "Switch", event.getTargetBlock().getPosition()))
                    event.setCancelled(true);
                else if (ContainersBlock.contains(event.getTargetBlock().getState().getType()) &&
                        !Core.getPermissionHandler().ableTo(player, Core.getChunkHandler().get(x / 16, z / 16), "Container", event.getTargetBlock().getPosition()))
                    event.setCancelled(true);
            }
        }
    }

    @Listener(order = Order.FIRST)
    public void             onBlockExplosion(ExplosionEvent.Post event) {
        for (Transaction<BlockSnapshot> transaction : event.getTransactions())
            transaction.setValid(false);
    }

    @Listener(order = Order.FIRST)
    public void             onBlockBurn(ChangeBlockEvent event) {
        List<Transaction<BlockSnapshot>> transactions = event.getTransactions();

        transactions.stream().filter(trans -> trans.getFinal().getState()
                .getType() == BlockTypes.FIRE).forEach(trans -> {
                    if (!(event.getCause().root() instanceof Player))
                        trans.setValid(false);
        });
    }

    private boolean         checkCuboPerms(Player player, ChangeBlockEvent event) {
        DBPlayer            dbPlayer = Core.getPlayerHandler().get(player);
        Cubo                cubo;
        int                 x, y, z;

        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockSnapshot   original = transaction.getOriginal();

            x = original.getLocation().get().getBlockX();
            y = original.getLocation().get().getBlockY();
            z = original.getLocation().get().getBlockZ();

            cubo = Core.getCuboHandler().get(new Vector3i(x, y, z));

            /*
            ** Pour chaque transaction, verification si le block est dans un cubo
            */
            if (cubo != null) {
                /*
                ** Le block est dans un cubo, on verifie les droits du joueur par rapport au cubo
                */
                if (!cubo.getInList().contains(dbPlayer)) {
                    /*
                    ** Le joueur n'a pas les droits mais il existe une exception en temps de guerre
                    */
                    if (Core.getWarHandler().Contains(dbPlayer) && Core.getWarHandler().ableTo(dbPlayer, cubo)) {
                        /*
                        ** Il est en guerre contre la ville propiétaire du cubo, on ajoute la transaction aux logs
                        */
                        Core.getWarHandler().getWar(dbPlayer).addRollbackBlock(transaction);
                    } else
                        return false;
                }
                /*
                ** Le joueur est autorisé à build cependant on verifie si c'est
                */
                if (Core.getWarHandler().ContainsDefender(cubo.getOwner().getCity()))
                    Core.getWarHandler().getWar(cubo.getOwner().getCity()).addRollbackBlock(transaction);
            }
        }
        return true;
    }

    private boolean         checkChunkPerms(Player player, ChangeBlockEvent event) {
        DBPlayer            dbPlayer = Core.getPlayerHandler().get(player);
        Chunk               chunk;
        int                 x, z;

        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockSnapshot   original = transaction.getOriginal();

            x = transaction.getOriginal().getLocation().get().getBlockX();
            z = transaction.getOriginal().getLocation().get().getBlockZ();

            chunk = Core.getChunkHandler().get(x / 16, z / 16);

            /*
            ** Pour chaque transaction, verification si le block est dans un chunk claim
            */
            if (chunk != null) {
                /*
                ** Le chunk est claim, verifions si le joueur peut build dessus
                */
                if (!Core.getPermissionHandler().ableTo(dbPlayer, chunk, Permissions.BUILD, original.getPosition())) {
                    /*
                    ** Le joueur n'a pas le droit mais une exception s'applique en cas de guerre
                    */
                    if (Core.getWarHandler().Contains(dbPlayer) && Core.getWarHandler().ableTo(dbPlayer, chunk)) {
                        /*
                        ** Le joueur est en guerre avec la ville owner du claim, on en profites pour ajouter
                        ** la transaction aux logs
                        */
                        Core.getWarHandler().getWar(dbPlayer).addRollbackBlock(transaction);
                    } else
                        return false;
                }
                /*
                ** Le joueur est autorisé à build cependant on verifie si la transaction doit être ajouter aux logs
                */
                if (Core.getWarHandler().ContainsDefender(chunk.getCity()))
                        Core.getWarHandler().getWar(chunk.getCity()).addRollbackBlock(transaction);
                }
        }

        /*
        ** L'ensemble des transactions on été verifié (et sauvegardé si besoin)
        */
        return true;
    }
}