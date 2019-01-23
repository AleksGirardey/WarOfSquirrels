package fr.craftandconquest.warofsquirrels.objects.dbobject;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalChunk;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class                Chunk extends DBObject {
    private static String   _fields = "`" + GlobalChunk.posX
            + "`, `" + GlobalChunk.posZ
            + "`, `" + GlobalChunk.name
            + "`, `" + GlobalChunk.cityId
            + "`, `" + GlobalChunk.homeblock
            + "`, `" + GlobalChunk.outpost
            + "`, `" + GlobalChunk.respawnX
            + "`, `" + GlobalChunk.respawnY
            + "`, `" + GlobalChunk.respawnZ
            + "`, `" + GlobalChunk.world + "`";

    private int         posX;
    private int         posZ;
    private String      name;
    private City        city;
    private Boolean     homeblock;
    private Boolean     outpost;
    private int         respawnX;
    private int         respawnY;
    private int         respawnZ;
    private World       world;

    public Chunk(DBPlayer player, boolean homeblock, boolean outpost) {
        this(player, homeblock, outpost, null);
    }

    public      Chunk(ResultSet rs) throws SQLException {
        super(GlobalChunk.id, GlobalChunk.tableName, _fields);
        this._primaryKeyValue = "" + rs.getInt(GlobalChunk.id);
        this.posX = rs.getInt(GlobalChunk.posX);
        this.posZ = rs.getInt(GlobalChunk.posZ);
        this.name = rs.getString(GlobalChunk.name);
        this.city = Core.getCityHandler().get(rs.getInt(GlobalChunk.cityId));
        this.homeblock = rs.getBoolean(GlobalChunk.homeblock);
        this.outpost = rs.getBoolean(GlobalChunk.outpost);
        this.world = Core.getPlugin().getServer().getWorld(UUID.fromString(rs.getString(GlobalChunk.world))).get();
        respawnX = (homeblock || outpost ? rs.getInt(GlobalChunk.respawnX) : 0);
        respawnY = (homeblock || outpost ? rs.getInt(GlobalChunk.respawnY) : 0);
        respawnZ = (homeblock || outpost ? rs.getInt(GlobalChunk.respawnZ) : 0);
        writeLog();
    }

    public      Chunk(DBPlayer player, boolean homeblock, boolean outpost, String name) {
        super(GlobalChunk.id, GlobalChunk.tableName, _fields);
        Player  p = player.getUser().getPlayer().get();

        this.posX = p.getLocation().getBlockX() / 16;
        this.posZ = p.getLocation().getBlockZ() / 16;
        this.city = player.getCity();
        this.homeblock = homeblock;
        this.outpost = outpost;
        this.name = name;
        if (homeblock || outpost) {
            respawnX = p.getLocation().getBlockX();
            respawnY = p.getLocation().getBlockY();
            respawnZ = p.getLocation().getBlockZ();
        }
        world = p.getWorld();
        this._primaryKeyValue = this.add("'" + posX + "', '"
                + posZ + "', "
                + (name == null ? "NULL" : "'" + name + "'") + ", '"
                + city.getId() + "',"
                + (homeblock ? "TRUE" : "FALSE") + ","
                + (outpost ? "TRUE" : "FALSE") + ","
                + (homeblock || outpost ? "'" + respawnX + "'" : "NULL") + ","
                + (homeblock || outpost? "'" + respawnY + "'" : "NULL") + ","
                + (homeblock || outpost? "'" + respawnZ + "'" : "NULL") + ",'"
                + world.getUniqueId().toString() + "'");
    }

    protected void      writeLog() {
        String          message = "";

        if (name != null) {
            Core.getLogger().info("[Chunk] '{}' created at [{};{}]", name, this.posX, this.posZ);
        } else {
            if (homeblock || outpost)
                message = " respawn at [" + respawnX + ";" + respawnY + ";" + respawnZ + "]";
            Core.getLogger().info("[Creating] Chunk at '{};{}' for {}[{};[}] in world {}{}", this.posX, this.posZ, this.getCity().getDisplayName(), homeblock ? "YES" : "NO", outpost ? "YES" : "NO", world.getName(), message);
        }
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

    public String   getName() { return name; }

    public void     setName(String name) {
        this.name = name;
        this.edit(GlobalChunk.name, name != null ? ("'" + name + "'") : "NULL");
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
        this.edit(GlobalChunk.respawnX, (respawnX == -1 ? "NULL" : "'" + respawnX + "'"));
    }

    public int      getRespawnY() { return respawnY; }

    public void     setRespawnY(int respawnY) {
        this.respawnY = respawnY;
        this.edit(GlobalChunk.respawnY, (respawnY == -1 ? "NULL" : "'" + respawnY + "'"));
    }

    public int      getRespawnZ() { return respawnZ; }

    public void     setRespawnZ(int respawnZ) {
        this.respawnZ = respawnZ;
        this.edit(GlobalChunk.respawnZ, (respawnZ == -1 ? "NULL" : "'" + respawnZ + "'"));
    }

    public World    getWorld() { return world; }

    public void     setWorld(World world) {
        this.world = world;
        this.edit(GlobalChunk.world, "'" + world.getUniqueId() + "'");
    }

    public boolean equals(Chunk chunk) { return chunk.getPosX() == this.posX && chunk.getPosZ() == this.posZ && chunk.getWorld().getUniqueId().equals(this.world.getUniqueId()); }

    @Override
    public String toString() {
        return ("[" + posX + ";" + posZ + "] in world " + world.getName());
    }
}
