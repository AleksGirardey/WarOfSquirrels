package fr.craftandconquest.warofsquirrels.objects.dbobject;

import com.flowpowered.math.vector.Vector3i;
import fr.craftandconquest.warofsquirrels.handlers.LoanHandler;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.cuboide.CuboVector;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalCubo;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalCuboAssociation;
import fr.craftandconquest.warofsquirrels.objects.database.Statement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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
    private Loan                loan;

    private static final String              fields = "`" + GlobalCubo.cityId
            + "`, `" + GlobalCubo.nom
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
        this.city = owner.getCity();
        this.owner = owner;
        this.vector = vector;
        this.priority = priority;
        this.permissionIn = in;
        this.permissionOut = out;
        this._primaryKeyValue = "" + this.add(
                city.getId()
                + ", '" + name
                + "', " + (parent != null ? parent.getId() : "NULL")
                + ", '" + owner.getId()
                + "', " + permissionIn.getId()
                + ", " + permissionOut.getId()
                + ", " + priority
                + ", " + vector.getA().getX()
                + ", " + vector.getA().getY()
                + ", " + vector.getA().getZ()
                + ", " + vector.getB().getX()
                + ", " + vector.getB().getY()
                + ", " + vector.getB().getZ() + "");
        this.inList.add(owner);
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
        this.inList.add(owner);
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
        Core.getLogger().info("[Creation] cubo set as '" + name + "' with parent '" + (parent != null ? parent.getName() : "NULL") + "' owned by '" + owner.getDisplayName() + "'.");
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
        this.edit(GlobalCubo.owner, "'" + owner.getId() + "'");
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
        this.edit(GlobalCubo.AposX, "" + vector.getA().getX());
        this.edit(GlobalCubo.AposY, "" + vector.getA().getY());
        this.edit(GlobalCubo.AposZ, "" + vector.getA().getZ());
        this.edit(GlobalCubo.AposX, "" + vector.getB().getX());
        this.edit(GlobalCubo.AposY, "" + vector.getB().getY());
        this.edit(GlobalCubo.AposZ, "" + vector.getB().getZ());
    }

    public void             add(DBPlayer player) { inList.add(player); }

    public List<DBPlayer>   getInList() { return inList; }

    public Loan             getLoan() { return loan; }

    public void             setLoan(Loan loan) { this.loan = loan; }

    public Text             toText() {
        return Text.of(TextColors.GOLD, this.name, TextColors.AQUA, " " + this.vector + " Propriétaire [",
                TextColors.GOLD, this.owner.getDisplayName(),
                TextColors.AQUA, "] Locataire [",
                TextColors.GOLD, (this.loan == null || this.loan.getLoaner() == null) ? "---" : this.loan.getLoaner().getDisplayName(),
                TextColors.RESET);
    }

    @Override
    public String           toString() {
        return toText().toPlain();
    }
}