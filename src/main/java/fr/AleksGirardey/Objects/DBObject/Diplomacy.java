package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalDiplomacy;
import fr.AleksGirardey.Objects.Database.GlobalPermission;

import javax.xml.ws.FaultAction;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Diplomacy extends DBObject {
    private static String   fields = "`" + GlobalDiplomacy.faction
            + "`, `" + GlobalDiplomacy.target
            + "`, `" + GlobalDiplomacy.relation
            + "`, `" + GlobalDiplomacy.permission + "`";

    private Faction         faction;
    private Faction         target;
    private boolean         relation;
    private Permission      permission;

    public          Diplomacy(Faction _faction, Faction _target, boolean _relation,
                              Permission _permission) {
        super(GlobalDiplomacy.id, GlobalDiplomacy.tableName, fields);
        
        faction = _faction;
        target = _target;
        relation = _relation;
        permission = _permission;
        this.add("" + _faction.getId() + ", "
                + _target.getId() + ", "
                + (_relation ? "TRUE" : "FALSE") + ", "
                + (_permission == null ? "NULL"  : _permission.getId()));
        writeLog();
    }

    public          Diplomacy(ResultSet rs) throws SQLException {
        super(GlobalDiplomacy.id, GlobalDiplomacy.tableName, fields);

        int         i;

        this._primaryKeyValue = "" + rs.getInt(GlobalDiplomacy.id);
        faction = Core.getFactionHandler().get(rs.getInt(GlobalDiplomacy.faction));
        target = Core.getFactionHandler().get(rs.getInt(GlobalDiplomacy.target));
        relation = rs.getBoolean(GlobalDiplomacy.relation);
        i = rs.getInt(GlobalDiplomacy.permission);
        permission = (i != 0 ? Core.getPermissionHandler().get(i) : null);
        writeLog();
    }

    public void     writeLog() {        
        Core.getLogger().info("[Creation] Diplomacy between '" + faction.getDisplayName() + "' and '" + target.getDisplayName() + "'");
    }

    public void             setFaction(Faction faction) {
        this.faction = faction;
        this.edit(GlobalDiplomacy.faction, "'" + faction.getId() + "'");
    }

    public void             setTarget(Faction target) {
        this.target = target;
        this.edit(GlobalDiplomacy.target, "'" + target.getId() + "'");
    }
    
    public void             setRelation(boolean relation) {
        this.relation = relation;
        this.edit(GlobalDiplomacy.relation, this.relation ? "TRUE" : "FALSE");
    }

    public void             setPermission(Permission permission) {
        this.permission = permission;
        this.edit(GlobalDiplomacy.permission, permission == null ? "NULL" : ("'" + permission.getId() + "'"));
    }

    public int              getId() {
        return Integer.parseInt(this._primaryKeyValue);
    }

    public Faction          getFaction() { return faction; }

    public Faction          getTarget() { return target; }

    public boolean          getRelation() {
        return relation;
    }

    public Permission       getPermissionMain() { return permission; }
}
