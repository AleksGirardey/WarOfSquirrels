package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Main;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalCity;
import fr.AleksGirardey.Objects.Database.GlobalPermission;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class City extends DBObject {
    private String      _primaryKeyName = GlobalCity.id;
    private String      _tableName = GlobalCity.tableName;
    private String      _fields = "`" + GlobalCity.displayName
            + "`, `" + GlobalCity.tag
            + "`, `" + GlobalCity.rank
            + "`, `" + GlobalCity.playerOwner
            + "`, `" + GlobalCity.permRes
            + "`, `" + GlobalCity.permAllies
            + "`, `" + GlobalCity.permOutside + "`";

    private String      displayName;
    private String      tag;
    private int         rank;
    private User        owner;
    private Permission  permRes;
    private Permission  permAllies;
    private Permission  permOutside;

    private Map<String, User> citizens = new HashMap<>();

    public City(String _displayName, Player _owner,
                Permission _res, Permission _allies, Permission _outside) {
        super();
        displayName = _displayName;
        tag = (displayName.length() <= 5 ? displayName : displayName.substring(0, 5));
        rank = 0;
        owner = _owner;
        permRes = _res;
        permAllies = _allies;
        permOutside = _outside;
        _primaryKeyValue = "" + this.add("`"
                + displayName + "`, `"
                + tag + "`, `"
                + "0`, `"
                + owner.getUniqueId().toString() + "`, `"
                + permRes.getId() + "`, `"
                + permAllies.getId() + "`, `"
                + permOutside.getId()+ "`");
    }

    public City(ResultSet rs) throws SQLException {
        super();
        _primaryKeyValue = "" + rs.getInt(GlobalCity.id);
        displayName = rs.getString(GlobalCity.displayName);
        tag = rs.getString(GlobalCity.tag);
        rank = rs.getInt(GlobalCity.rank);
        owner = Core.getPlayerHandler().getUser(
                UUID.fromString(
                        rs.getString(GlobalCity.playerOwner))).orElse(null);
        permRes = Core.getPermissionHandler().get(rs.getInt(GlobalCity.permRes));
        permAllies = Core.getPermissionHandler().get(rs.getInt(GlobalCity.permAllies));
        permOutside = Core.getPermissionHandler().get(rs.getInt(GlobalCity.permOutside));

        //get Players name with this ID;
        this.populate();
    }

    private void        populate() {
        
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.edit(GlobalCity.displayName, displayName);
    }

    public void setTag(String tag) {
        this.tag = tag;
        this.edit(GlobalCity.tag, tag);
    }

    public void setRank(int rank) {
        this.rank = rank;
        this.edit(GlobalCity.rank, "" + rank);
    }

    public void setOwner(Player owner) {
        this.owner = owner;
        this.edit(GlobalCity.playerOwner, owner.getUniqueId().toString());
    }

    public void setPermRes(Permission permRes) {
        this.permRes = permRes;
        this.edit(GlobalCity.permRes, "" + permRes.getId());
    }

    public void setPermAllies(Permission permAllies) {
        this.permAllies = permAllies;
        this.edit(GlobalCity.permAllies, "" + permAllies.getId());
    }

    public void setPermOutside(Permission permOutside) {
        this.permOutside = permOutside;
        this.edit(GlobalCity.permOutside, "" + permOutside.getId());
    }

    public int      getId() {
        return      Integer.parseInt(_primaryKeyValue);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTag() {
        return tag;
    }

    public int getRank() {
        return rank;
    }

    public Player getOwner() {
        return owner;
    }

    public Permission getPermRes() {
        return permRes;
    }

    public Permission getPermAllies() {
        return permAllies;
    }

    public Permission getPermOutside() {
        return permOutside;
    }
}