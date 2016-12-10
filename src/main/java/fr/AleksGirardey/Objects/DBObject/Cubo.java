package fr.AleksGirardey.Objects.DBObject;

import com.flowpowered.math.vector.Vector3i;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Cuboide.CuboVector;
import fr.AleksGirardey.Objects.Database.GlobalCubo;
import fr.AleksGirardey.Objects.Database.GlobalCuboAssociation;
import fr.AleksGirardey.Objects.Database.Statement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class            Cubo extends DBObject {
    private String      name;
    private City        city;
    private Cubo        parent;
    private DBPlayer    owner;
    private Permission  permissionIn;
    private Permission  permissionOut;
    private int         priority;
    private CuboVector  vector;

    private int                 parentId;
    private List<DBPlayer>      inList = new ArrayList<>();

    private static final String              fields = "`" + GlobalCubo.nom
            + "`, `" + GlobalCubo.cityId
            + "`, `" + GlobalCubo.parent
            + "`, `" + GlobalCubo.owner
            + "`, `" + GlobalCubo.pInlist
            + "`, `" + GlobalCubo.pOutside
            + "`, `" + GlobalCubo.priority
            + "`, `" + GlobalCubo.AposX
            + "`, `" + GlobalCubo.AposY
            + "`, `" + GlobalCubo.AposZ
            + "`, `" + GlobalCubo.BposX
            + "`, `" + GlobalCubo.BposY
            + "`, `" + GlobalCubo.BposZ + "`";

    public              Cubo(String name, Cubo parent, DBPlayer owner,
                             Permission in, Permission out, int priority, CuboVector vector) {
        super(GlobalCubo.id, GlobalCubo.tableName, fields);
        this.name = name;
        this.parent = parent;
        this.owner = owner;
        this.vector = vector;
        this.priority = priority;
        this.permissionIn = in;
        this.permissionOut = out;
        this._primaryKeyValue = "" + this.add("`" + name
                + "`, `" + (parent != null ? parent.getId() : "NULL")
                + "`, `" + owner.getId()
                + "`, `" + permissionIn.getId()
                + "`, `" + permissionOut.getId()
                + "`, `" + priority
                + "`, `" + vector.getOne().getX()
                + "`, `" + vector.getOne().getY()
                + "`, `" + vector.getOne().getZ()
                + "`, `" + vector.getEight().getX()
                + "`, `" + vector.getEight().getY()
                + "`, `" + vector.getEight().getZ() + "`");
        writeLog();
    }

    public              Cubo(ResultSet rs) throws SQLException {
        super(GlobalCubo.id, GlobalCubo.tableName, fields);
        this._primaryKeyValue = "" + rs.getInt(GlobalCubo.id);
        this.city = Core.getCityHandler().get(rs.getInt(GlobalCubo.cityId));
        this.name = rs.getString(GlobalCubo.nom);
        this.parentId = rs.getInt(GlobalCubo.parent);
        this.owner = Core.getPlayerHandler().get(rs.getString(GlobalCubo.owner));
        this.permissionIn = Core.getPermissionHandler().get(rs.getInt(GlobalCubo.pInlist));
        this.permissionOut = Core.getPermissionHandler().get(rs.getInt(GlobalCubo.pOutside));
        this.priority = rs.getInt(GlobalCubo.priority);
        this.vector = new CuboVector(
                new Vector3i(
                        rs.getInt(GlobalCubo.AposX),
                        rs.getInt(GlobalCubo.AposY),
                        rs.getInt(GlobalCubo.AposZ)),
                new Vector3i(
                        rs.getInt(GlobalCubo.BposX),
                        rs.getInt(GlobalCubo.BposY),
                        rs.getInt(GlobalCubo.BposZ)));

        this.populate();
        writeLog();
    }

    private void         populate() throws SQLException {
        String          sql = "SELECT * FROM `" + GlobalCuboAssociation.tableName + "`" +
                " WHERE `" + GlobalCuboAssociation.cuboId + "` = " + this._primaryKeyValue;

        Statement statement = new Statement(sql);
        statement.Execute();
        while (statement.getResult().next())
            this.inList.add(Core.getPlayerHandler().get(
                    statement.getResult().getString(GlobalCuboAssociation.playerUuid)
            ));
        statement.Close();
    }

    public void         writeLog() {
        Core.getLogger().info("[Creation] Cubo set as '" + name + "' with parent '" + (parent != null ? parent.getName() : "NULL") + "' owned by '" + owner.getDisplayName() + "'.");
    }

    public void         updateDependencies() {
        if (parentId != 0)
            this.parent = Core.getCuboHandler().get(parentId);
    }

    public boolean      contains (Vector3i block) {
        return vector.contains(block);
    }

    public int          getId() { return Integer.parseInt(_primaryKeyValue); }

    public String       getName() { return name; }

    public City         getCity() { return city; }

    public void         setName(String name) {
        this.name = name;
        this.edit(GlobalCubo.nom, name);
    }

    public Cubo         getParent() { return parent; }

    public void         setParent(Cubo parent) {
        this.parent = parent;
        this.edit(GlobalCubo.parent, "" + parent.getId());
    }

    public DBPlayer     getOwner() { return owner; }

    public void         setOwner(DBPlayer owner) {
        this.owner = owner;
        this.edit(GlobalCubo.owner, owner.getId());
    }

    public Permission   getPermissionIn() { return permissionIn; }

    public void         setPermissionIn(Permission permissionIn) {
        this.permissionIn = permissionIn;
        this.edit(GlobalCubo.pInlist, "" + permissionIn.getId());
    }

    public Permission   getPermissionOut() { return permissionOut; }

    public void         setPermissionOut(Permission permissionOut) {
        this.permissionOut = permissionOut;
        this.edit(GlobalCubo.pOutside, "" + permissionOut.getId());
    }

    public int          getPriority() { return priority; }

    public void         setPriority(int priority) {
        this.priority = priority;
        this.edit(GlobalCubo.priority, "" + priority);
    }

    public CuboVector   getVector() { return vector; }

    public void         setVector(CuboVector vector) {
        this.vector = vector;
        this.edit(GlobalCubo.AposX, "" + vector.getOne().getX());
        this.edit(GlobalCubo.AposY, "" + vector.getOne().getY());
        this.edit(GlobalCubo.AposZ, "" + vector.getOne().getZ());
        this.edit(GlobalCubo.AposX, "" + vector.getEight().getX());
        this.edit(GlobalCubo.AposY, "" + vector.getEight().getY());
        this.edit(GlobalCubo.AposZ, "" + vector.getEight().getZ());
    }

    public void             add(DBPlayer player) { inList.add(player); }

    public List<DBPlayer>   getInList() { return inList; }
}