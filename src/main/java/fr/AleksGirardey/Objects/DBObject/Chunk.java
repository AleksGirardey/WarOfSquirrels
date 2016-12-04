package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalChunk;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Chunk extends DBObject {
    private int         posX;
    private int         posZ;
    private City        city;
    private Boolean     homeblock;
    private Boolean     outpost;
    private int         respawnX;
    private int         respawnY;
    private int         respawnZ;

    public Chunk(DBPlayer player, boolean _homeblock, boolean _outpost) {
        super(GlobalChunk.id, GlobalChunk.tableName, "`" + GlobalChunk.posX
                + "`, `" + GlobalChunk.posZ
                + "`, `" + GlobalChunk.cityId
                + "`, `" + GlobalChunk.homeblock
                + "`, `" + GlobalChunk.outpost
                + "`, `" + GlobalChunk.respawnX
                + "`, `" + GlobalChunk.respawnY
                + "`, `" + GlobalChunk.respawnZ + "`");
        Player  p = player.getUser().getPlayer().get();

        posX = p.getLocation().getBlockX() / 16;
        posZ = p.getLocation().getBlockZ() / 16;
        city = player.getCity();
        homeblock = _homeblock;
        outpost = _outpost;
        if (homeblock || outpost) {
            respawnX = p.getLocation().getBlockX();
            respawnY = p.getLocation().getBlockY();
            respawnZ = p.getLocation().getBlockZ();
        }
        this.add("'" + posX + "', '"
                + posZ + "', '"
                + city.getId() + "',"
                + (homeblock ? "TRUE" : "FALSE") + ","
                + (outpost ? "TRUE" : "FALSE") + ","
                + (homeblock || outpost ? "'" + respawnX + "'" : "NULL") + ","
                + (homeblock || outpost? "'" + respawnY + "'" : "NULL") + ","
                + (homeblock || outpost? "'" + respawnZ + "'" : "NULL"));
    }

    public      Chunk(ResultSet rs) throws SQLException {
        super(GlobalChunk.id, GlobalChunk.tableName, "`" + GlobalChunk.posX
                + "`, `" + GlobalChunk.posZ
                + "`, `" + GlobalChunk.cityId
                + "`, `" + GlobalChunk.homeblock
                + "`, `" + GlobalChunk.outpost
                + "`, `" + GlobalChunk.respawnX
                + "`, `" + GlobalChunk.respawnY
                + "`, `" + GlobalChunk.respawnZ + "`");
        this._primaryKeyValue = "" + rs.getInt(GlobalChunk.id);
        this.posX = rs.getInt(GlobalChunk.posX);
        this.posZ = rs.getInt(GlobalChunk.posZ);
        this.city = Core.getCityHandler().get(rs.getInt(GlobalChunk.cityId));
        this.homeblock = rs.getBoolean(GlobalChunk.homeblock);
        this.outpost = rs.getBoolean(GlobalChunk.outpost);
        respawnX = (homeblock || outpost ? rs.getInt(GlobalChunk.respawnX) : 0);
        respawnY = (homeblock || outpost ? rs.getInt(GlobalChunk.respawnY) : 0);
        respawnZ = (homeblock || outpost ? rs.getInt(GlobalChunk.respawnZ) : 0);
    }

    protected void      writeLog() {
        String          message = "";

        if (homeblock || outpost)
            message = " respawn at [" + respawnX + ";" + respawnY + ";" + respawnZ + "]";
        Core.getLogger().info("[Creating] Chunk at '" + this.posX + ";" + this.posZ + "' for "
                + this.getCity().getDisplayName() + "[" + (homeblock ? "YES" : "NO") + ";" + (outpost ? "YES" : "NO") + "]" + message);
    }

    public int      getId() { return Integer.parseInt(_primaryKeyValue); }

    public int      getPosX() { return posX; }

    public void     setPosX(int posX) {
        this.posX = posX;
        this.edit(GlobalChunk.posX, "'" + posX + "'");
    }

    public int      getPosZ() { return posZ; }

    public void     setPosZ(int posZ) {
        this.posZ = posZ;
        this.edit(GlobalChunk.posZ, "'" + posZ + "'");
    }

    public City     getCity() { return city; }

    public void setCity(City city) {
        this.city = city;
        this.edit(GlobalChunk.cityId, "'" + city.getId() + "'");
    }

    public Boolean  isHomeblock() { return homeblock; }

    public void     setHomeblock(Boolean homeblock) {
        this.homeblock = homeblock;
        this.edit(GlobalChunk.homeblock, homeblock ? "TRUE" : "FALSE");
    }

    public Boolean  isOutpost() { return outpost; }

    public void     setOutpost(Boolean outpost) {
        this.outpost = outpost;
        this.edit(GlobalChunk.outpost, outpost ? "TRUE" : "FALSE");
    }

    public int      getRespawnX() { return respawnX; }

    public void     setRespawnX(int respawnX) {
        this.respawnX = respawnX;
        this.edit(GlobalChunk.respawnX, "'" + respawnX + "'");
    }

    public int      getRespawnY() { return respawnY; }

    public void     setRespawnY(int respawnY) {
        this.respawnY = respawnY;
        this.edit(GlobalChunk.respawnY, "'" + respawnY + "'");
    }

    public int      getRespawnZ() { return respawnZ; }

    public void     setRespawnZ(int respawnZ) {
        this.respawnZ = respawnZ;
        this.edit(GlobalChunk.respawnZ, "'" + respawnZ + "'");
    }

    public boolean equals(Chunk chunk)
    {
        return chunk.getPosX() == this.posX && chunk.getPosZ() == this.posZ;
    }

    @Override
    public String toString() {
        return ("[" + posX + ";" + posZ + "]");
    }
}
