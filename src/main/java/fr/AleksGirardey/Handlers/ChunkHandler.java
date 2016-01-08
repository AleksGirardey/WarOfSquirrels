package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Chunk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChunkHandler {

    public      ChunkHandler() {
    }

    public boolean          exists(int posX, int posZ) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;
        boolean             bool = false;

        try {
            String          sql = "SELECT `chunk_id` FROM `Chunk` " +
                    "WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, posX);
            statement.setInt(2, posZ);
            bool = statement.executeQuery().first();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
        return (bool);
    }

    public void             add(int posX, int posZ, int id) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;

        try {
            String          sql = "INSERT INTO `Chunk`(`chunk_posX`, `chunk_posZ`, `chunk_cityId`) " +
                    "VALUES (? , ? , ?);";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, posX);
            statement.setInt(2, posZ);
            statement.setInt(3, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
    }

    public void                 addOutpost(int posX, int posZ) throws SQLException {
        Connection              c = null;
        PreparedStatement       statement = null;

        try {
            String              sql = "UPDATE `Chunk` SET `chunk_outpost` = TRUE WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, posX);
            statement.setInt(2, posZ);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
    }

    public void                 addHomeblock(int posX, int posZ) throws SQLException {
        Connection              c = null;
        PreparedStatement       statement = null;

        try {
            String              sql = "UPDATE `Chunk` SET `chunk_homeblock` = TRUE WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, posX);
            statement.setInt(2, posZ);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
    }

    public boolean              isOutpost(int posX, int posZ) throws SQLException {
        Connection              c = null;
        PreparedStatement       statement = null;
        ResultSet               rs = null;
        boolean                 res = false;

        try {
            String              sql = "SELECT `chunk_outpost` FROM `Chunk` WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, posX);
            statement.setInt(2, posZ);
            rs = statement.executeQuery();
            if (rs.next())
                res = rs.getBoolean("chunk_outpost");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public boolean              isHomeblock(int posX, int posZ) throws SQLException {
        Connection              c = null;
        PreparedStatement       statement = null;
        ResultSet               rs = null;
        boolean                 res = false;

        try {
            String              sql = "SELECT `chunk_homeblock` FROM `Chunk` WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, posX);
            statement.setInt(2, posZ);
            rs = statement.executeQuery();
            if (rs.next())
                res = rs.getBoolean("chunk_homeblock");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public int               getCity(int posX, int posZ) throws SQLException {
        Connection              c = null;
        PreparedStatement       statement = null;
        ResultSet               rs = null;
        int                     res = 0;

        try {
            String          sql = "SELECT `chunk_cityId` FROM `Chunk`" +
                    " WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, posX);
            statement.setInt(2, posZ);
            rs = statement.executeQuery();
            if (rs.next()) {
                res = rs.getInt("chunk_cityId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public int               getCity(int id) throws SQLException {
        Connection              c = null;
        PreparedStatement       statement = null;
        ResultSet               rs = null;
        int                     res = 0;

        try {
            String          sql = "SELECT `chunk_cityId` FROM `Chunk`" +
                    " WHERE `chunk_id` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            if (rs.next()) {
                res = rs.getInt("chunk_cityId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public void             delete(int x, int z) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;

        try {
            String          sql = "DELETE FROM `Chunk` WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, x);
            statement.setInt(2, z);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
    }

    public String             getId(int x, int z) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet           rs = null;
        String              id = null;

        try {
            String          sql = "SELECT `chunk_id` FROM `Chunk` WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, x);
            statement.setInt(2, z);
            rs = statement.executeQuery();
            if (rs.next())
                id = rs.getString("chunk_id");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (id);
    }

    public void             deleteCity(int id) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;

        try {
            String          sql = "DELETE FROM `Chunk` WHERE `chunk_cityId` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
    }

    public boolean             setSpawn(Chunk chunk, int x, int y, int z) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;

        try {
            if (this.isHomeblock(chunk.getX(), chunk.getZ()) || this.isOutpost(chunk.getX(), chunk.getZ())) {
                String      sql = "UPDATE `Chunk` SET `chunk_respawnX` = ?, `chunk_respawnY` = ?, `chunk_respawnZ` = ? " +
                        "WHERE `chunk_posX` = ? AND `chunk_posZ` = ?;";

                c = Core.getDatabaseHandler().getConnection();
                statement = c.prepareStatement(sql);
                statement.setInt(1, x);
                statement.setInt(2, y);
                statement.setInt(3, z);
                statement.setInt(4, chunk.getX());
                statement.setInt(5, chunk.getZ());
                statement.executeUpdate();
            } else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return (false);
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
        return (true);
    }
}