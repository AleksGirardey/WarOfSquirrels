
package fr.AleksGirardey.Objects.Utilitaires;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Cubo;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Database.Statement;
import fr.AleksGirardey.Objects.City.InfoCity;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Utils {

    @Deprecated
    public static int       getPlayerPos(DBPlayer player, String pos) {
        if (pos.equals("X"))
            return player.getUser().getPlayer().get().getLocation().getBlockX();
        else if (pos.equals("Y"))
            return player.getUser().getPlayer().get().getLocation().getBlockY();
        else
            return player.getUser().getPlayer().get().getLocation().getBlockZ();
    }

    public static String    getListFromTableString(Map<Integer, Pair<Integer, String>> list) {
        String  res = "";
        int i = 0;

        for (Map.Entry<Integer, Pair<Integer, String>> entry : list.entrySet()) {
            res += entry.getValue().getR();
            if (i != (list.size() - 1))
                res += ", ";
            i++;
        }
        return res;
    }

    public static String        getStringFromUuidList(List<String> list) {
        String      res = "";
        int         i = 0;

        for (String str : list) {
            if (i != 0 && i != list.size() - 1)
                res += ", ";
            res += Core.getPlayerHandler().get(str).getDisplayName();
            i++;
        }
        return res;
    }

    public static String        getListFromTableString(String[][] list, int index) {
        String res = "";
        int i = 0;

        while (i < list.length) {
            res += list[i][index];
            if (i != list.length - 1)
                res += ", ";
            i++;
        }
        return (res);
    }

    public static boolean       checkCityName(String name) {
        Statement               statement= null;
        String                  sql = "SELECT `city_displayName` FROM `City` WHERE `city_displayName` = ?;";

        if (!name.matches("[A-Za-z0-9]+") || name.length() > 34)
            return (false);
        try {
            statement = new Statement(sql);
            statement.getStatement().setString(1, name);
            if (statement.Execute().first())
                return (false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (true);
    }

    public static Location<World>   getNearestSpawn(DBPlayer player) {
        List<Chunk>                 chunks = new ArrayList<>();
        Location<World>             pLocation = player.getUser().getPlayer().get().getLocation(),
                save = null;
        Vector3d                    chunk = null;

        chunks.addAll(Core.getChunkHandler().getOupostList(player.getCity()));
        chunks.add(Core.getChunkHandler().getHomeblock(player.getCity()));

        for (Chunk c : chunks) {
            Vector3d    vec = new Vector3d(
                    c.getRespawnX(),
                    c.getRespawnY(),
                    c.getRespawnZ());
            if (save == null || (pLocation.getPosition().distance(chunk) < pLocation.getPosition().distance(save.getPosition())))
                save = player.getUser().getPlayer().get().getWorld().getLocation(vec);
        }
        return (save);
    }

    public static String getStringFromPlayerList(List<DBPlayer> list) {
        String  res = "";
        int i = 0;

        for (DBPlayer p : list) {
            res += p.getDisplayName();
            if (i != list.size() - 1)
                res += ", ";
        }
        return res;
    }

    public static Text          getTownChatTag(DBPlayer player) {
        return (Text.of("[" + player.getDisplayName() + "]"));
    }

    public static Text          getChatTag(DBPlayer player) {
        InfoCity                city;
        Text                    tag;

        if (player.getCity() != null) {
            city = Core.getInfoCityMap().get(player.getCity());
            String      displayName = player.getDisplayName();

            if (player.getCity().getOwner() == player)
                displayName = city.getRank().getPrefixMayor() + " " + displayName;
            else if (player.isAssistant())
                displayName = city.getRank().getPrefixAssistant() + " " + displayName;

            tag = Text.of(city.getColor(), "[" + player.getCity().getTag()
                    + "][" + displayName
                    + "]", TextColors.RESET);
        } else {
            tag = Text.of(TextColors.GRAY, "[Wanderer][" + player.getDisplayName() + "]", TextColors.RESET);
        }

        if (player.getUser().getPlayer().get().hasPermission("minecraft.command.op"))
            tag = Text.of(TextColors.DARK_RED, "[Admin]", TextColors.RESET, tag);
        return tag;
    }

    public static int       NearestHomeblock(int posX, int posZ) {
        Double              closerDistance = 30.0;
        Vector2d            def = new Vector2d(posX, posZ);

        for (Chunk c : Core.getChunkHandler().getHomeblockList()) {
            Vector2d        vec = new Vector2d(c.getPosX(), c.getPosZ());
            Double          dist = vec.distance(def);

            if (closerDistance == 0 || closerDistance > dist)
                closerDistance = dist;
        }
        return closerDistance.intValue();
    }

    public static boolean checkCuboName(String s, City city) {
        for (Cubo c : Core.getCuboHandler().getFromCity(city)) {
            if (c.getName().equalsIgnoreCase(s))
                return false;
        }
        return true;
    }

    public static boolean       canOffer(Inventory inventory, ItemStack itemStack) {
        Inventory               i = Inventory.builder().of(InventoryArchetypes.PLAYER).build(Core.getPlugin());

        for (Inventory slot : inventory.slots()) {
            Optional<ItemStack>     item = slot.peek();

            item.ifPresent(itemStack1 -> i.offer(itemStack1.copy()));
        }

        return i.offer(itemStack).getRejectedItems().size() <= 0;
    }
}
