package fr.AleksGirardey.Objects.DBObject;

import com.flowpowered.math.vector.Vector3i;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalShop;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class Shop extends DBObject {
    private static String      _fields = "`" + GlobalShop.player
            + "`, `" + GlobalShop.signX
            + "`, `" + GlobalShop.signY
            + "`, `" + GlobalShop.signZ
            + "`, `" + GlobalShop.chestX
            + "`, `" + GlobalShop.chestY
            + "`, `" + GlobalShop.chestZ
            + "`, `" + GlobalShop.itemId
            + "`, `" + GlobalShop.boughtPrice
            + "`, `" + GlobalShop.sellPrice
            + "`, `" + GlobalShop.quantity
            + "`, `" + GlobalShop.world + "`";

    /* -- DB Fields -- */
    private DBPlayer    player;
    private Vector3i    signLocation;
    private Vector3i    chestLocation;
    private ItemType    item;
    private int         boughtPrice;
    private int         sellPrice;
    private int         quantity;
    private World       world;

    /* -- Extra fields -- */
    private Sign        sign;
    private Chest       chest;

    /*
    ** Constructors
    */

    public  Shop(DBPlayer _player, Sign sign, Chest chest,
                String itemId, int bPrice, int sPrice, int _quantity) {
        super(GlobalShop.id, GlobalShop.tableName, _fields);

        player = _player;
        signLocation = sign.getLocation().getBlockPosition();
        chestLocation = chest.getLocation().getBlockPosition();
        item = Core.getPlugin().getRegistry().getType(ItemType.class, itemId).orElse(null);
        boughtPrice = bPrice;
        sellPrice = sPrice;
        quantity = _quantity;
        world = sign.getWorld();
        this._primaryKeyValue = "" + this.add("'" + player.getId()
                + "', " + signLocation.getX()
                + ", " + signLocation.getY()
                + ", " + signLocation.getZ()
                + ", " + chestLocation.getX()
                + ", " + chestLocation.getY()
                + ", " + chestLocation.getZ()
                + ", '" + item.getId()
                + "', " + boughtPrice
                + ", " + sellPrice
                + ", " + quantity
                + ", '" + world.getUniqueId().toString() + "'");

        this.sign = sign;
        this.chest = chest;

        writeLog();
    }

    public      Shop(ResultSet rs) throws SQLException {
        super(GlobalShop.id, GlobalShop.tableName, _fields);

        this._primaryKeyValue = rs.getString(GlobalShop.id);
        player = Core.getPlayerHandler().get(rs.getString(GlobalShop.player));
        signLocation = new Vector3i(rs.getInt(GlobalShop.signX),
                rs.getInt(GlobalShop.signY), rs.getInt(GlobalShop.signZ));
        chestLocation = new Vector3i(rs.getInt(GlobalShop.chestX),
                rs.getInt(GlobalShop.chestY), rs.getInt(GlobalShop.chestZ));
        item = Core.getPlugin().getRegistry().getType(ItemType.class, rs.getString(GlobalShop.itemId)).orElse(null);
        boughtPrice = rs.getInt(GlobalShop.boughtPrice);
        sellPrice = rs.getInt(GlobalShop.sellPrice);
        quantity = rs.getInt(GlobalShop.quantity);
        world = Core.getPlugin().getServer().getWorld(UUID.fromString(rs.getString(GlobalShop.world))).orElse(null);
        sign = (Sign) world.getLocation(signLocation).getTileEntity().orElse(null);
        chest = (Chest) world.getLocation(chestLocation).getTileEntity().orElse(null);
        writeLog();
    }

    /*
    ** Functions
    */



    /*
    ** Getters and Setters
    */

    public DBPlayer getPlayer() { return player; }

    public void setPlayer(DBPlayer player) {
        this.player = player;
        this.edit(GlobalShop.player, player.getId());
    }

    public Vector3i getSignLocation() { return signLocation; }

    public void setSignLocation(Vector3i signLocation) {
        this.signLocation = signLocation;
        this._sql = "UPDATE `" + _tablename + "` SET"
                + " `" + _tablename + "`.`" + GlobalShop.signX + "` = " + signLocation.getX() + ","
                + " `" + _tablename + "`.`" + GlobalShop.signY + "` = " + signLocation.getY() + ","
                + " `" + _tablename + "`.`" + GlobalShop.signZ + "` = " + signLocation.getZ()
                + " WHERE `" + _primaryKeyValue + "` = '" + _primaryKeyValue + "';";
        this.update();
    }

    public Vector3i getChestLocation() { return chestLocation; }

    public void setChestLocation(Vector3i chestLocation) {
        this.chestLocation = chestLocation;
        this._sql = "UPDATE `" + _tablename + "` SET"
                + " `" + _tablename + "`.`" + GlobalShop.chestX + "` = " + chestLocation.getX() + ","
                + " `" + _tablename + "`.`" + GlobalShop.chestY + "` = " + chestLocation.getY() + ","
                + " `" + _tablename + "`.`" + GlobalShop.chestZ + "` = " + chestLocation.getZ()
                + " WHERE `" + _primaryKeyValue + "` = '" + _primaryKeyValue + "';";
        this.update();
    }

    public ItemType getItem() { return item; }

    public void setItem(ItemType item) {
        this.item = item;
        this.edit(GlobalShop.itemId, item.getId());
    }

    public int getBoughtPrice() { return boughtPrice; }

    public void setBoughtPrice(int boughtPrice) {
        this.boughtPrice = boughtPrice;
        this.edit(GlobalShop.boughtPrice, "" + boughtPrice);
    }

    public int getSellPrice() { return sellPrice; }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
        this.edit(GlobalShop.sellPrice, "" + sellPrice);
    }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.edit(GlobalShop.quantity, "" + quantity);
    }

    public Sign getSign() { return sign; }

    public void setSign(Sign sign) {
        this.sign = sign;
        this.setSignLocation(sign.getLocation().getBlockPosition());
    }

    public Chest getChest() { return chest; }

    public void setChest(Chest chest) {
        this.chest = chest;
        this.setChestLocation(chest.getLocation().getBlockPosition());
    }

    public void                 buy(Player player) {
        DBPlayer                dbPlayer = Core.getPlayerHandler().get(player);
        ItemStack               itemStack;

        itemStack = ItemStack
                .builder()
                .itemType(this.item)
                .quantity(this.quantity)
                .build();

        if (dbPlayer.getBalance() < this.getBoughtPrice()) {
            dbPlayer.sendMessage(Text.of(TextColors.RED, "You don't have enough money to buy this.", TextColors.RESET));
            return;
        } else if (!Utils.canOffer(player.getInventory(), itemStack.copy())) {
            dbPlayer.sendMessage(Text.of(TextColors.RED, "You don't have enough space to buy this.", TextColors.RESET));
            return;
        }

        Optional<Inventory>     doubleChest = this.chest.getDoubleChestInventory();
        Inventory               inventory = doubleChest.orElse(this.chest.getInventory());

        Optional<ItemStack>     peek = inventory.query(this.item).peek(this.quantity);

        if (peek.isPresent()) {
            if (peek.get().getQuantity() != quantity) {
                dbPlayer.sendMessage(Text.of(TextColors.RED, "Shop haven't enough supplies to sell you that.", TextColors.RESET));
                return;
            }
            player.getInventory().offer(itemStack);
            inventory.query(this.item).poll(this.quantity);
            dbPlayer.sendMessage(Text.of(TextColors.GREEN, "You bought " + quantity + " of " + item.getName() + "."));
            this.player.sendMessage(Text.of(TextColors.GREEN, dbPlayer.getDisplayName() + " bought at your shop " + quantity + " of " + item.getName() + "."));
        } else
            dbPlayer.sendMessage(Text.of(TextColors.RED, "Shop is Out Of Stock !", TextColors.RESET));
    }

    public void             sell(Player player) {
        DBPlayer            dbPlayer = Core.getPlayerHandler().get(player);
        ItemStack           itemStack;

        itemStack = ItemStack
                .builder()
                .itemType(this.item)
                .quantity(this.quantity)
                .build();

        if (this.player.getBalance() < this.getSellPrice()) {
            dbPlayer.sendMessage(Text.of(TextColors.RED, "Shop doesn't have enough money to bought you this.", TextColors.RESET));
            return;
        } else if (!Utils.canOffer(chest.getDoubleChestInventory().orElse(chest.getInventory()), itemStack.copy())) {
            dbPlayer.sendMessage(Text.of(TextColors.RED, "Shop doesn't have enough space to bought you this", TextColors.RESET));
            return;
        }

        Inventory inventory = player.getInventory();

        Optional<ItemStack> peek = inventory.query(this.item).peek(this.quantity);

        if (peek.isPresent()) {
            if (peek.get().getQuantity() != quantity) {
                dbPlayer.sendMessage(Text.of(TextColors.RED, "You don't have enough items to sell.", TextColors.RESET));
                return;
            }
            chest.getDoubleChestInventory().orElse(this.chest.getInventory()).offer(itemStack);
            inventory.query(this.item).poll(this.quantity);
            dbPlayer.sendMessage(Text.of(TextColors.GREEN, "You sold " + quantity + " of " + item.getName() + "."));
            this.player.sendMessage(Text.of(TextColors.GREEN, dbPlayer.getDisplayName() + " sold you " + quantity + item.getName() + "."));
        } else
            dbPlayer.sendMessage(Text.of(TextColors.RED, "You don't have enough to sell this.", TextColors.RESET));
    }

    @Override
    protected void writeLog() {
        Core.getLogger().info("[Shop] (" + _fields + ") : #" + _primaryKeyValue
                + "," + player.getDisplayName() + ", ["
                + signLocation.getX() + ";" + signLocation.getY() + ";" + signLocation.getZ()
                + "],[" + chestLocation.getX() + ";" + chestLocation.getY() + ";" + chestLocation.getZ() + "],"
                + item.getName() + "," + boughtPrice + "," + sellPrice + "," + quantity);
    }

    public void actualize() {
        Optional<SignData>      optSignData = sign.get(SignData.class);

        if (optSignData.isPresent()) {
            final SignData d = optSignData.get();

            d.set(d.getValue(Keys.SIGN_LINES).get().set(0, Text.of("[" + player.getDisplayName() + "]")));
            d.set(d.getValue(Keys.SIGN_LINES).get().set(1, Text.of(item.getName())));
            d.set(d.getValue(Keys.SIGN_LINES).get().set(2, Text.of("B > " + boughtPrice + ":" + sellPrice + " < S")));
            d.set(d.getValue(Keys.SIGN_LINES).get().set(3, Text.of("Quantity : " + quantity)));

            sign.offer(d);
        }
    }
}
