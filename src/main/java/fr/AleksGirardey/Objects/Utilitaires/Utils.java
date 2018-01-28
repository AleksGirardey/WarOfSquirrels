
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
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

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

        chunks.addAll(Core.getChunkHandler().getOupostList(player.getCity()));
        chunks.add(Core.getChunkHandler().getHomeblock(player.getCity()));

        Core.getLogger().warn("[Spawn] Chunks size : " + chunks.size());

        for (Chunk c : chunks) {
            if (c.getWorld().getUniqueId() == player.getUser().getPlayer().get().getWorld().getUniqueId()) {
                Vector3d vec = new Vector3d(
                        c.getRespawnX(),
                        c.getRespawnY(),
                        c.getRespawnZ());
                if (save == null || (pLocation.getPosition().distance(save.getPosition()) > pLocation.getPosition().distance(vec))) {
                    save = player.getUser().getPlayer().get().getWorld().getLocation(vec);
                }
            }
        }
        if (save == null) {
            Chunk hb = Core.getChunkHandler().getHomeblock(player.getCity());
            return hb.getWorld().getLocation(new Vector3d(hb.getRespawnX(), hb.getRespawnY(), hb.getRespawnZ()));
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
        Inventory duplicate = Inventory.builder().from(inventory).build(Core.getMain());

        return duplicate.offer(itemStack).getRejectedItems().isEmpty();
    }

    public static String toTime(int value) {
        int                 minutes, seconds;
        String              res = "";

        Core.getLogger().debug("[ToTime] value : " + value);
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
        Collection<Player>      onlinePlayers = Core.getPlugin().getServer().getOnlinePlayers(), wanderers = new ArrayList<>();
        Text.Builder            message = Text.builder().append(Text.of(TextColors.BLUE, "=========[ " + onlinePlayers.size() + " joueur(s) en ligne ]=========\n", TextColors.RESET));
        int                     j = 0, sizeMap = map.keySet().size();

        for (City city : map.keySet()) {
            int size;
            List<DBPlayer>      onlines = Core.getCityHandler().getOnlineDBPlayers(city);

            size = onlines.size();
            if (size != 0) {
                message.append(Text.of(
                        Core.getCityHandler().getCityMap().get(city).getColor(),
                        "[" + city.getDisplayName() + "] "));
                buildCitizenList(message, city, onlines);
            }
            if (j <= sizeMap - 1)
                message.append(Text.of("\n", TextColors.RESET));
            j++;
        }

        for (Player player : onlinePlayers) {
            if (Core.getPlayerHandler().get(player).getCity() == null)
                wanderers.add(player);
        }
        if (wanderers.size() > 0) {
            message.append(Text.of(TextColors.GRAY, "[Vagabons] "));
            int i = 0;
            for (Player p : wanderers) {
                message.append(Text.of(Core.getPlayerHandler().get(p).getDisplayName()));
                if (i != wanderers.size() - 1)
                    message.append(Text.of(", "));
            }
        }
        message.append(Text.of(TextColors.RESET));
        pl.sendMessage(message.build());
    }

    private static void buildCitizenList(Text.Builder message, City city, List<DBPlayer> onlines) {
        int i = 0;
        DBPlayer mayor = null;
        List<DBPlayer> assistants = new ArrayList<>(), residents = new ArrayList<>();

        for (DBPlayer player : onlines) {
            if (player.getCity().getOwner() == player)
                mayor = player;
            else if (player.isAssistant())
                assistants.add(player);
            else
                residents.add(player);
        }
        message.append(Text.of(Core.getCityHandler().getCityMap().get(city).getCityRank().getPrefixMayor() + " " + mayor.getDisplayName()));
        if (assistants.size() > 0) message.append(Text.of(", "));
        for (DBPlayer player : assistants) {
            message.append(Text.of(Core.getCityHandler().getCityMap().get(city).getCityRank().getPrefixAssistant() + " " + player.getDisplayName()));
            if (i != assistants.size() - 1)
                message.append(Text.of(", "));
            i++;
        }
        if (residents.size() > 0) message.append(Text.of(", "));
        i = 0;
        for (DBPlayer player : residents) {
            message.append(Text.of(player.getDisplayName()));
            if (i != residents.size() - 1)
                message.append(Text.of(", "));
            i++;
        }
    }

    public static void replaceContainer(BlockSnapshot block) {
        TileEntity              ti;

        if (block.getLocation().get().getTileEntity().isPresent()) {
            Core.getLogger().warn("FOUND A TILEENTITYCARRIER");
            ti = block.getLocation().get().getTileEntity().orElse(null);
            if (ti instanceof TileEntityCarrier) {
                TileEntityCarrier carrier = (TileEntityCarrier) ti;
                Inventory inventory = carrier.getInventory();
                int sizeB = inventory.size(), sizeA;
                inventory.clear();
                sizeA = inventory.size();
                Core.getLogger().warn("Before : " + sizeB + " and then " + sizeA);
            }
        }
    }

    /*
    ** Défini si l'élément City est dans une position
    ** qui est attaquable par la faction donnée.
    */
    public static boolean Attackable(City city, Faction faction) {
        return true;
    }

    public static String toStringFromList(List<String> factionNameList) {
        int i = 0;
        StringBuilder message = new StringBuilder();

        for (String faction : factionNameList) {
            message.append(faction);
            if (i != factionNameList.size() - 1) message.append(", ");
            i++;
        }
        return message.toString();
    }

    public static boolean checkShopFormat(SignData datas) {
        Text tag = datas.lines().get(0),
            id = datas.lines().get(1),
            price = datas.lines().get(2),
            quantity = datas.lines().get(3);

        Pattern patternId = Pattern.compile("[0-9]+");
        Pattern patternPrice = Pattern.compile("[0-9]+:[0-9]+");

        return tag.equals(Text.of("[Shop]")) &&
                /*(patternIdOne.matcher(id.toString()).matches() ||
                        patternPrice.matcher(id.toString()).matches()) &&*/
                !id.toString().equals("") &&
                patternPrice.matcher(price.toPlain()).matches() &&
                patternId.matcher(quantity.toPlain()).matches();
    }

    public static Inventory duplicateInventory(Inventory inventory) {
        Inventory   duplicate = Inventory.builder().of(InventoryArchetypes.CHEST).build(Core.getMain());

        for (Inventory slot : inventory.slots()) {
            ItemStack onSlot = slot.peek().orElse(null);
            if (onSlot == null) continue;
            duplicate.offer(onSlot.copy());
        }
        return duplicate;
    }
}
