package fr.AleksGirardey.Objects.Utilitaires;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Cuboide.Cubo;
import fr.AleksGirardey.Objects.Database.Statement;
import fr.AleksGirardey.Objects.City.InfoCity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Utils {

    @Deprecated
    public static int getPlayerPos(Player player, String pos) {
        if (pos.equals("X"))
            return player.getLocation().getBlockX();
        else if (pos.equals("Y"))
            return player.getLocation().getBlockY();
        else
            return player.getLocation().getBlockZ();
    }

    public static String getListFromTableString(Map<Integer, Pair<Integer, String>> list) {
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

    public static String getStringFromUuidList(List<String> list) {
        String      res = "";
        int         i = 0;

        for (String str : list) {
            if (i != 0 && i != list.size() - 1)
                res += ", ";
            res += Core.getPlayerHandler().<String>getElement(str, "player_displayName");
            i++;
        }
        return res;
    }

    public static String getListFromTableString(String[][] list, int index) {
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

    public static boolean checkCityName(String name) {
        Statement statement= null;
        String sql = "SELECT `city_displayName` FROM `City` WHERE `city_displayName` = ?;";

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

    public static Location<World> getNearestSpawn(Player player) {
        PlayerHandler plh = Core.getPlayerHandler();
        Statement statement = null;

        Location<World> pLocation = player.getLocation(), save = null;
        Vector3d chunk = null;
        String sql = "SELECT `chunk_respawnX`, `chunk_respawnY`, `chunk_respawnZ` FROM `Chunk` WHERE `chunk_cityId` = ? AND (`chunk_homeblock` = TRUE OR `chunk_outpost` = TRUE);";

        try {
            statement = new Statement(sql);
            statement.getStatement().setInt(1, plh.<Integer>getElement(player, "player_cityId"));
            statement.Execute();
            while (statement.getResult().next()) {
                chunk = new Vector3d(
                        statement.getResult().getDouble("chunk_respawnX"),
                        statement.getResult().getDouble("chunk_respawnY"),
                        statement.getResult().getDouble("chunk_respawnZ"));
                if (save == null || pLocation.getPosition().distance(chunk) < pLocation.getPosition().distance(save.getPosition())) {
                    save = player.getWorld().getLocation(chunk);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (save);
    }

    public static String getStringFromPlayerList(List<Player> list) {
        String  res = "";
        int i = 0;

        for (Player p : list) {
            res += Core.getPlayerHandler().<String>getElement(p, "player_displayName");
            if (i != list.size() - 1)
                res += ", ";
        }
        return res;
    }

    public static Text getChatTag(Player player) {
        InfoCity city;
        Text            tag;

        if (Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") != null) {
            city = Core.getInfoCityMap().get(Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"));
            String      displayName = Core.getPlayerHandler().<String>getElement(player, "player_displayName");

            if (Core.getPlayerHandler().isOwner(player))
                displayName = city.getRank().getPrefixMayor() + " " + displayName;
            else if (Core.getCityHandler().getAssistants(city.getCityId()).contains(displayName))
                displayName = city.getRank().getPrefixAssistant() + " " + displayName;

            tag = Text.of(city.getColor(), "[" + Core.getCityHandler().<String>getElement(city.getCityId(), "city_tag")
                    + "][" + displayName
                    + "]", TextColors.RESET);
        } else
            tag = Text.of(TextColors.GRAY, "[Wanderer][" + Core.getPlayerHandler().<String>getElement(player, "player_displayName") + "]", TextColors.RESET);

        if (player.hasPermission("minecraft.command.op"))
            tag = Text.of(TextColors.DARK_RED, "[Admin]", TextColors.RESET, tag);
        return tag;
    }

    public static int NearestHomeblock(Chunk chunk) {
        Double          closerDistance = 30.0;
        Vector2d        def = new Vector2d(chunk.getX(), chunk.getZ());

        for (Chunk c : Core.getChunkHandler().getHomeblockList()) {
            Vector2d        vec = new Vector2d(c.getX(), c.getZ());
            Double          dist = vec.distance(def);

            if (closerDistance == 0 || closerDistance > dist)
                closerDistance = dist;
        }
        return closerDistance.intValue();
    }

    public static boolean checkCuboName(String s, int cityId) {
        for (Cubo c : Core.getCuboHandler().getFromCity(cityId)) {
            if (c.getName().equalsIgnoreCase(s))
                return false;
        }
        return true;
    }
}
