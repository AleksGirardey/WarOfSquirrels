
package fr.AleksGirardey.Objects.Utilitaires;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import fr.AleksGirardey.Objects.DBObject.*;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalFaction;
import fr.AleksGirardey.Objects.Database.Statement;
import fr.AleksGirardey.Objects.City.InfoCity;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.entity.living.player.Player;
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

    private static List<BlockType>  containers;

    static {
        containers = new ArrayList<>();
        containers.add(BlockTypes.CHEST);
        containers.add(BlockTypes.TRAPPED_CHEST);
        containers.add(BlockTypes.BREWING_STAND);
        containers.add(BlockTypes.DISPENSER);
        containers.add(BlockTypes.DROPPER);
        containers.add(BlockTypes.FURNACE);
        containers.add(BlockTypes.HOPPER);
    }

    @Deprecated
    public static int       getPlayerPos(DBPlayer player, String pos) {
        switch (pos) {
            case "X":
                return player.getUser().getPlayer().get().getLocation().getBlockX();
            case "Y":
                return player.getUser().getPlayer().get().getLocation().getBlockY();
            default:
                return player.getUser().getPlayer().get().getLocation().getBlockZ();
        }
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

        if (!name.matches("[A-Za-z]+") || name.length() > 34)
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

    public static boolean       checkFactionName(String factionName) {
        Statement               statement;
        String                  sql = "SELECT `" + GlobalFaction.displayName + "` FROM `" + GlobalFaction.tableName
                + "` WHERE `" + GlobalFaction.displayName + "` = '" + factionName + "';";

        if (!factionName.matches("[A-Za-z]+") || factionName.length() > 34)
            return false;
        try {
            statement = new Statement(sql);
            statement.Execute();
            if (statement.getResult().first())
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Location<World>   getNearestSpawn(DBPlayer player) {
        List<Chunk>                 chunks = new ArrayList<>();
        Location<World>             pLocation = player.getUser().getPlayer().get().getLocation(),
                save = null;
        Vector3d                    chunk = null;

        chunks.addAll(Core.getChunkHandler().getOupostList(player.getCity()));
        chunks.add(Core.getChunkHandler().getHomeblock(player.getCity()));

        for (Chunk c : chunks) {
            if (c.getWorld() == player.getUser().getPlayer().get().getWorld()) {
                Vector3d vec = new Vector3d(
                        c.getRespawnX(),
                        c.getRespawnY(),
                        c.getRespawnZ());
                if (save == null || (pLocation.getPosition().distance(chunk) < pLocation.getPosition().distance(save.getPosition())))
                    save = player.getUser().getPlayer().get().getWorld().getLocation(vec);
            }
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
                displayName = city.getCityRank().getPrefixMayor() + " " + displayName;
            else if (player.isAssistant())
                displayName = city.getCityRank().getPrefixAssistant() + " " + displayName;

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

    public static boolean       CanPlaceOutpost(int posX, int posZ) {
        int                     value = NearestHomeblock(posX, posZ);

        return (value == -1 || value >= Core.getConfig().getDistanceOutpost());
    }

    public static boolean   CanPlaceCity(int posX, int posZ) {
        int                 value = NearestHomeblock(posX, posZ);

        return (value == -1 || value >= Core.getConfig().getDistanceCities());
    }

    public static int       NearestHomeblock(int posX, int posZ) {
        Double              closerDistance = Double.MAX_VALUE; // = Double.parseDouble("" + ConfigLoader.distanceCities);
        Vector2d            playerChunk = new Vector2d(posX, posZ);
        List<Chunk>         homeblockList;

        homeblockList = Core.getChunkHandler().getHomeblockList();
        if (homeblockList.size() == 0)
            return (-1);
        for (Chunk c : homeblockList) {
            Vector2d        vec = new Vector2d(c.getPosX(), c.getPosZ());
            Double          dist = vec.distance(playerChunk);

            closerDistance = Double.min(dist, closerDistance);
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

    public static String toTime(int value) {
        int                 minutes, seconds;
        String              res = "";


        minutes = value / 60;
        seconds = value % 60;
        if (minutes > 0)
            res = minutes + " min ";
        res += seconds + " s";

        return res;
    }

    public static void  displayCommandList(Player commandSource) {
        DBPlayer        pl = Core.getPlayerHandler().get(commandSource);
        Map<City, InfoCity>     map = Core.getCityHandler().getCityMap();
        Text            message = Text.of(TextColors.BLUE);
        int                     j = 0, sizeMap = map.keySet().size();

        for (City city : map.keySet()) {
            int                 i = 0, size;
            List<DBPlayer>      onlines = Core.getCityHandler().getOnlineDBPlayers(city);

            size = onlines.size();
            message.concat(Text.of("[" + city.getDisplayName() + "] "));
            for (DBPlayer player : onlines) {
                if (city.getOwner() == player)
                    message.concat(Text.of(map.get(city).getCityRank().getPrefixMayor() + " "));
                message.concat(Text.of(player.getDisplayName()));
                if (i <= size - 1)
                    message.concat(Text.of(", "));
                i++;
            }
            if (j <= sizeMap - 1)
                message.concat(Text.of("\n"));
        }

        pl.sendMessage(message);
    }

    public static void replaceContainer(BlockSnapshot block) {
        TileEntity              ti;

        ti = block.getLocation().get().getTileEntity().orElse(null);
        if (ti instanceof TileEntityCarrier) {
            TileEntityCarrier   carrier = (TileEntityCarrier) ti;
            carrier.getInventory().clear();
        }
    }

    /*
    ** Défini si l'élément City est dans une position
    ** qui est attaquable par la faction donnée.
    */
    public static boolean Attackable(City city, Faction faction) {
        return true;
    }
}
