package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalCity;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import fr.AleksGirardey.Objects.Database.Statement;
import org.spongepowered.api.entity.living.player.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    private DBPlayer    owner;
    private Permission  permRes;
    private Permission  permAllies;
    private Permission  permOutside;

    private Map<String, DBPlayer> citizens = new HashMap<>();

    public City(String _displayName, DBPlayer _owner,
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
                + owner.getUser().getUniqueId().toString() + "`, `"
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
        owner = Core.getPlayerHandler().get(rs.getString(GlobalCity.playerOwner));
        permRes = Core.getPermissionHandler().get(rs.getInt(GlobalCity.permRes));
        permAllies = Core.getPermissionHandler().get(rs.getInt(GlobalCity.permAllies));
        permOutside = Core.getPermissionHandler().get(rs.getInt(GlobalCity.permOutside));

        //get Players name with this ID;
        this.populate();
    }

    private void        populate() {
        String          sql = "SELECT * FROM `" + GlobalPlayer.tableName + "` WHERE `"
                + GlobalPlayer.cityId + "` = " + _primaryKeyValue;

        try {
            Statement   statement = new Statement(sql);
            statement.Execute();

            while (statement.getResult().next()) {
                ResultSet rs = statement.getResult();
                citizens.put(rs.getString(GlobalPlayer.displayName),
                        Core.getPlayerHandler().get(rs.getString(GlobalPlayer.uuid)));
            }
            statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public void setOwner(DBPlayer owner) {
        this.owner = owner;
        this.edit(GlobalCity.playerOwner, owner.getUser().getUniqueId().toString());
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

    public DBPlayer getOwner() {
        return owner;
    }

    public Permission getPermRes() {
        return permRes;
    }

    public Permission getPermAllies() {
        return permAllies;
    }

    public Permission   getPermOutside() {
        return permOutside;
    }

    public Collection<DBPlayer> getCitizens() { return citizens.values(); }

    public Collection<DBPlayer> getAssistants() {
        return getCitizens().stream().filter(DBPlayer::isAssistant).collect(Collectors.toCollection(ArrayList::new));
    }

    public Boolean      contains(User user) {
        return citizens.containsValue(user);
    }
}