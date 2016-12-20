package fr.AleksGirardey.Handlers;

import com.flowpowered.math.vector.Vector3i;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Shop;
import fr.AleksGirardey.Objects.Database.GlobalShop;
import fr.AleksGirardey.Objects.Database.Statement;
import org.slf4j.Logger;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ShopHandler {
    private Logger              logger;

    private Map<Sign, Shop>     shops = new HashMap<>();

    public ShopHandler(Logger logger) { this.logger =  logger; }

    public void         populate() {
        String          sql = "SELECT * FROM `" + GlobalShop.tableName + "`;";
        Shop            shop;

        try {
            Statement   statement = new Statement(sql);
            statement.Execute();
            while (statement.getResult().next()) {
                shop = new Shop(statement.getResult());
                this.shops.put(shop.getSign(), shop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void         add(DBPlayer player, SignData datas, Sign sign, Chest chest) {
        ItemType        itemType;
        Text            prices;
        String[]        tablePrice;
        String          item;

        item = datas.lines().get(1).toPlain();
        itemType = Core.getPlugin().getRegistry().getType(ItemType.class, item).orElse(null);
        prices = datas.lines().get(2);
        tablePrice = prices.toPlain().split(":");
        if (itemType == null) {
            player.sendMessage(Text.of(TextColors.RED, "Wrong value set as Item", TextColors.RESET));
            return;
        }

        Shop            shop = new Shop(
                player,
                sign, chest,
                datas.lines().get(1).toPlain(),
                Integer.parseInt(tablePrice[0]),
                Integer.parseInt(tablePrice[1]),
                Integer.parseInt(datas.lines().get(3).toPlain()));
        this.shops.put(sign, shop);

        final SignData      d = sign.get(SignData.class).get();

        d.set(d.getValue(Keys.SIGN_LINES).get().set(0, Text.of("[" + player.getDisplayName() + "]")));
        d.set(d.getValue(Keys.SIGN_LINES).get().set(1, Text.of(datas.lines().get(1).toPlain())));
        d.set(d.getValue(Keys.SIGN_LINES).get().set(2, Text.of("B > " + shop.getBoughtPrice() + ":" + shop.getSellPrice() + " < S")));
        d.set(d.getValue(Keys.SIGN_LINES).get().set(3, Text.of("Quantity : " + shop.getQuantity())));

        Core.getPlugin()
                .getScheduler()
                .createTaskBuilder().execute(() -> sign.offer(d))
                .delay(1, TimeUnit.SECONDS)
                .submit(Core.getMain());
    }

    public Shop         get(Vector3i loc) {
        for (Shop shop : shops.values())
            if (shop.getSignLocation().equals(loc))
                return shop;
        return null;
    }
}