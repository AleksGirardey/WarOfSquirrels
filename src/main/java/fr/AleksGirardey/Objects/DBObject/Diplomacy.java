package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Database.GlobalDiplomacy;

import java.sql.ResultSet;

public class Diplomacy extends DBObject {
    private String      _primaryKeyName = GlobalDiplomacy.id;
    private String      _tableName = GlobalDiplomacy.tableName;
    private String      _fields = "`" + GlobalDiplomacy.main
            + "`, `" + GlobalDiplomacy.sub
            + "`, `" + GlobalDiplomacy.relation
            + "`, `" + GlobalDiplomacy.permId + "`";

    private City            main;
    private City            sub;
    private boolean         relation;
    private Permission      permission;

    public Diplomacy(City _main, City _sub, boolean _relation, Permission _perm) {
        super();
        main = _main;
        sub = _sub;
        relation = _relation;
        this._primaryKeyValue = "" + this.add("`"
                + _main.getId() + "`, `"
                + _sub.getId() + "`, `"
                + (_relation ? "TRUE" : "FALSE") + "`, `"
                + _perm.getId() + "`");
    }

    public Diplomacy(ResultSet rs) {

    }

    public void setMain(City main) {
        this.main = main;
        this.edit(GlobalDiplomacy.main, "" + this.main.getId());
    }

    public void setSub(City sub) {
        this.sub = sub;
        this.edit(GlobalDiplomacy.sub, "" + this.sub.getId());
    }

    public void setRelation(boolean relation) {
        this.relation = relation;
        this.edit(GlobalDiplomacy.relation, this.relation ? "TRUE" : "FALSE");
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
        this.edit(GlobalDiplomacy.permId, "" + permission.getId());
    }

    public int  getId() {
        return Integer.parseInt(this._primaryKeyValue);
    }

    public City getMain() {
        return main;
    }

    public City getSub() {
        return sub;
    }

    public boolean getRelation() {
        return relation;
    }

    public Permission getPermission() {
        return permission;
    }
}