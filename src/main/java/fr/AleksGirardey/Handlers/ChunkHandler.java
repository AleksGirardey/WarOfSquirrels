package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Statement;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChunkHandler {

    private Statement   _statement;
    
    public      ChunkHandler() {
        _statement = new Statement();
    }

    public boolean          exists(int posX, int posZ){
        boolean             bool = false;
        String              sql = "SELECT `chunk_id` FROM `Chunk` "
                                    + "WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, posX);
            _statement.getStatement().setInt(2, posZ);
            bool = _statement.Execute().first();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (bool);
    }

    public boolean          exists(Chunk chunk)
    { return exists(chunk.getX(), chunk.getZ()); }

    public void             add(int posX, int posZ, int id) {
            String          sql = "INSERT INTO `Chunk`(`chunk_posX`, `chunk_posZ`, `chunk_cityId`) " +
                    "VALUES (? , ? , ?);";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, posX);
            _statement.getStatement().setInt(2, posZ);
            _statement.getStatement().setInt(3, id);
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void                 addOutpost(Player player, int posX, int posZ) {
        String              sql = "INSERT INTO `Chunk`(`chunk_posX`, `chunk_posZ`, `chunk_cityId`, `chunk_outpost`, `chunk_respawnZ`, `chunk_respawnY`, `chunk_respawnZ`)"
                                    + "VALUES (?, ?, ?, TRUE, ?, ?, ?);";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, posX);
            _statement.getStatement().setInt(2, posZ);
            _statement.getStatement().setInt(3, Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"));
            _statement.getStatement().setInt(4, player.getLocation().getBlockX());
            _statement.getStatement().setInt(5, player.getLocation().getBlockY());
            _statement.getStatement().setInt(6, player.getLocation().getBlockZ());
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void                 addHomeblock(int posX, int posZ) {
        String                  sql = "UPDATE `Chunk` SET `chunk_homeblock` = TRUE WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, posX);
            _statement.getStatement().setInt(2, posZ);
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean              isOutpost(int posX, int posZ) {
        String                  sql = "SELECT `chunk_outpost` FROM `Chunk` WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";
        boolean                 res = false;

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, posX);
            _statement.getStatement().setInt(2, posZ);
            if (_statement.Execute().next())
                res = _statement.getResult().getBoolean("chunk_outpost");
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (res);
    }

    public boolean              isHomeblock(int posX, int posZ) {
        String                  sql = "SELECT `chunk_homeblock` FROM `Chunk` WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";
        boolean                 res = false;

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, posX);
            _statement.getStatement().setInt(2, posZ);
            if (_statement.Execute().next())
                res = _statement.getResult().getBoolean("chunk_homeblock");
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (res);
    }

    public int               getCity(int posX, int posZ) {
        int                     res = 0;
        String                  sql = "SELECT `chunk_cityId` FROM `Chunk`" +
                                " WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, posX);
            _statement.getStatement().setInt(2, posZ);
            if (_statement.Execute().next())
                res = _statement.getResult().getInt("chunk_cityId");
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (res);
    }

    public int              getId(int x, int z) {
        int                 id = 0;
        String              sql = "SELECT `chunk_id` FROM `Chunk` WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, x);
            _statement.getStatement().setInt(2, z);
            if (_statement.Execute().next())
                id = _statement.getResult().getInt("chunk_id");
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (id);
    }

    public int              getCity(int id) {
        int                 res = 0;
        String              sql = "SELECT `chunk_cityId` FROM `Chunk`" +
                            " WHERE `chunk_id` = ?;";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, id);
            if (_statement.Execute().next()) {
                res = _statement.getResult().getInt("chunk_cityId");
            }
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (res);
    }

    public void             delete(int x, int z) {
        String              sql = "DELETE FROM `Chunk` WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, x);
            _statement.getStatement().setInt(2, z);
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void             deleteCity(int id) {
            String          sql = "DELETE FROM `Chunk` WHERE `chunk_cityId` = ?;";

        try{
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, id);
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean             setSpawn(Chunk chunk, int x, int y, int z) {
        try {
            String      sql = "UPDATE `Chunk` SET `chunk_respawnX` = ?, `chunk_respawnY` = ?, `chunk_respawnZ` = ? " +
                    "WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, x);
            _statement.getStatement().setInt(2, y);
            _statement.getStatement().setInt(3, z);
            _statement.getStatement().setInt(4, chunk.getX());
            _statement.getStatement().setInt(5, chunk.getZ());
            _statement.Update();
            _statement.Close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean canBePlaced(int x, int z, boolean b) {
        return true;
    }
}