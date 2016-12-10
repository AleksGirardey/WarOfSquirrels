package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalCuboAssociation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class            CuboAssociation extends DBObject {
    private Cubo        cubo;
    private DBPlayer    player;
    private Permission  permission;

    private static final String     fields = "`" + GlobalCuboAssociation.cuboId
            + "`, `" + GlobalCuboAssociation.playerUuid
            + "`, `" + GlobalCuboAssociation.permissionId + "`";

    public      CuboAssociation(Cubo cubo, DBPlayer player, Permission permission) {
        super(GlobalCuboAssociation.id, GlobalCuboAssociation.tableName, fields);
        this.cubo = cubo;
        this.player = player;
        this.permission = permission;
        this._primaryKeyValue = "" + this.add("`" + cubo.getId()
                + "`, `" + player.getId()
                + "`, " + (permission != null ? "`" + permission.getId() + "`" : "NULL"));
        writeLog();
    }

    public      CuboAssociation(ResultSet rs) throws SQLException {
        super(GlobalCuboAssociation.id, GlobalCuboAssociation.tableName, fields);
        this.cubo = Core.getCuboHandler().get(rs.getInt(GlobalCuboAssociation.cuboId));
        this.player = Core.getPlayerHandler().get(rs.getString(GlobalCuboAssociation.playerUuid));
        this.permission = Core.getPermissionHandler().get(rs.getInt(GlobalCuboAssociation.permissionId));
        writeLog();
    }

    @Override
    protected void writeLog() {

    }

    public int          getId() { return Integer.parseInt(_primaryKeyValue); }

    public Cubo         getCubo() { return cubo; }

    public DBPlayer     getPlayer() { return player; }

    public Permission   getPermission() { return permission; }
}
