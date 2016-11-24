package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalDiplomacy;
import fr.AleksGirardey.Objects.Database.GlobalPermission;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Diplomacy extends DBObject {
    private String      _primaryKeyName = GlobalDiplomacy.id;
    private String      _tableName = GlobalDiplomacy.tableName;
    private String      _fields = "`" + GlobalDiplomacy.main
            + "`, `" + GlobalDiplomacy.sub
            + "`, `" + GlobalDiplomacy.relation
            + "`, `" + GlobalDiplomacy.permMain
            + "`, `" + GlobalDiplomacy.permSub + "`";

    private City            main;
    private City            sub;
    private boolean         relation;
    private Permission      permissionMain;
    private Permission      permissionSub;

    public          Diplomacy(City _main, City _sub, boolean _relation, Permission _perm) {
        super();
        main = _main;
        sub = _sub;
        relation = _relation;
        permissionMain = _perm;
        this._primaryKeyValue = "" + this.add("`"
                + _main.getId() + "`, `"
                + _sub.getId() + "`, `"
                + (_relation ? "TRUE" : "FALSE") + "`, `"
                + (_perm == null ? "0"  : _perm.getId()) + "`");
    }

    public          Diplomacy(ResultSet rs) throws SQLException {
        super();
        int         i;

        this._primaryKeyValue = "" + rs.getInt(GlobalDiplomacy.id);
        main = Core.getCityHandler().get(rs.getInt(GlobalDiplomacy.main));
        sub = Core.getCityHandler().get(rs.getInt(GlobalDiplomacy.sub));
        relation = rs.getBoolean(GlobalDiplomacy.relation);
        i = rs.getInt(GlobalDiplomacy.permMain);
        permissionMain = (i != 0 ? Core.getPermissionHandler().get(i) : null);
        i = rs.getInt(GlobalDiplomacy.permSub);
        permissionSub = (i != 0 ? Core.getPermissionHandler().get(i) : null);
    }

    public void             setMain(City main) {
        this.main = main;
        this.edit(GlobalDiplomacy.main, "" + this.main.getId());
    }

    public void             setSub(City sub) {
        this.sub = sub;
        this.edit(GlobalDiplomacy.sub, "" + this.sub.getId());
    }

    public void             setRelation(boolean relation) {
        this.relation = relation;
        this.edit(GlobalDiplomacy.relation, this.relation ? "TRUE" : "FALSE");
    }

    public void             setPermissionMain(Permission permission) {
        this.permissionMain = permission;
        this.edit(GlobalDiplomacy.permMain, "" + permission.getId());
    }

    public void             setPermissionSub(Permission permission) {
        this.permissionSub = permission;
        this.edit(GlobalDiplomacy.permSub, "" + permission.getId());
    }

    public int              getId() {
        return Integer.parseInt(this._primaryKeyValue);
    }

    public City             getMain() {
        return main;
    }

    public City             getSub() {
        return sub;
    }

    public boolean          getRelation() {
        return relation;
    }

    public Permission       getPermissionMain() { return permissionMain; }

    public Permission       getPermissionSub() { return permissionSub; }
}
