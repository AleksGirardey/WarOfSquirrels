package fr.craftandconquest.warofsquirrels.objects.dbobject;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalTerritory;
import org.spongepowered.api.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Territory extends DBObject {

    private static final String FIELDS = "`" + GlobalTerritory.NAME
            + "`, `" + GlobalTerritory.POSX
            + "`, `" + GlobalTerritory.POSZ
            + "`, `" + GlobalTerritory.FACTIONID
            + "`, `" + GlobalTerritory.BASTIONID
            + "`, `" + GlobalTerritory.WORLD + "`";

    private String      name;
    private int         posX;
    private int         posZ;
    private Faction     faction;
    private int         bastion;
    private World       world;

    private Territory(String name, int posX, int posZ, Faction faction, int bastion, World world) {
        super(GlobalTerritory.ID, GlobalTerritory.TABLENAME, FIELDS);

        this.name = name;
        this.posX = posX;
        this.posZ = posZ;
        this.faction = faction;
        this.bastion = bastion;
        this.world = world;
        writeLog();
    }

    public Territory(ResultSet rs) throws SQLException {
        this(
                rs.getString(GlobalTerritory.NAME),
                rs.getInt(GlobalTerritory.POSX),
                rs.getInt(GlobalTerritory.POSZ),
                Core.getFactionHandler().get(rs.getInt(GlobalTerritory.FACTIONID)),
                rs.getInt(GlobalTerritory.BASTIONID),
                Core.getPlugin().getServer().getWorld(UUID.fromString(rs.getString(GlobalTerritory.WORLD))).get()
        );
        this._primaryKeyValue = "" + rs.getInt(GlobalTerritory.ID);
    }

    public Territory(String name, int posX, int posZ, Faction faction, World world) {
        this(name, posX, posZ, faction, 0, world);
        this._primaryKeyValue = this.add("'" + name
                + "', '" + posX
                + "', '" + posZ
                + "', " + (faction == null ? "NULL" : "'" + faction.getId() + "'")
                + ", NULL" +
                ", '" + world.getUniqueId() + "'");
    }

    @Override
    protected void writeLog() {
        Core.getLogger().info("[Territory] '{0}' created at [{1},{2}][{3}] with owner '{4}'",
                name, posX, posZ, world.getName(), faction.getDisplayName());
    }

    public int  getId() { return Integer.parseInt(_primaryKeyValue); }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.edit(GlobalTerritory.NAME, "'" + name + "'");
    }

    public int  getPosX() { return posX; }
    public void setPosX(int posX) {
        this.posX = posX;
        this.edit(GlobalTerritory.POSX, "'" + posX + "'");
    }

    public int  getPosZ() { return posZ; }
    public void setPosZ(int posZ) {
        this.posZ = posZ;
        this.edit(GlobalTerritory.POSZ, "'" + posZ + "'");
    }

    public Faction  getFaction() { return faction; }
    public void     setFaction(Faction faction) {
        this.faction = faction;
        this.edit(GlobalTerritory.FACTIONID, "'" + faction.getId() + "'");
    }

    public int  getBastion() { return bastion; }
    public void setBastion(int bastion) {
        this.bastion = bastion;
        this.edit(GlobalTerritory.BASTIONID, "'" + bastion + "'");
    }

    public World    getWorld() { return world; }
    public void     setWorld(World world) {
        this.world = world;
        this.edit(GlobalTerritory.WORLD, "'" + world.getUniqueId() + "'");
    }
}
