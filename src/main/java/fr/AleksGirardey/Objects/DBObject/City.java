package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalCity;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import fr.AleksGirardey.Objects.Database.Statement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class City extends DBObject {
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
        super(GlobalCity.id, GlobalCity.tableName, "`" + GlobalCity.displayName
                + "`, `" + GlobalCity.tag
                + "`, `" + GlobalCity.rank
                + "`, `" + GlobalCity.playerOwner
                + "`, `" + GlobalCity.permRes
                + "`, `" + GlobalCity.permAllies
                + "`, `" + GlobalCity.permOutside + "`");
        displayName = _displayName;
        tag = (displayName.length() <= 5 ? displayName : displayName.substring(0, 5));
        rank = 0;
        owner = _owner;
        permRes = _res;
        permAllies = _allies;
        permOutside = _outside;
        this.add("'" + displayName + "', '"
                + tag + "', '0', '"
                + owner.getUser().getUniqueId().toString() + "', '"
                + permRes.getId() + "', '"
                + permAllies.getId() + "', '"
                + permOutside.getId()+ "'");
        writeLog();
    }

    public City(ResultSet rs) throws SQLException {
        super(GlobalCity.id, GlobalCity.tableName, "`" + GlobalCity.displayName
                + "`, `" + GlobalCity.tag
                + "`, `" + GlobalCity.rank
                + "`, `" + GlobalCity.playerOwner
                + "`, `" + GlobalCity.permRes
                + "`, `" + GlobalCity.permAllies
                + "`, `" + GlobalCity.permOutside + "`");
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
        writeLog();
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

    public void     writeLog() {
        Core.getLogger().info("[Creating] City '" + displayName + "' aka '" + tag + "' is rank " + rank
                + " and ruled by " + owner.getDisplayName() + " with permissions : "
                + permRes.toString() + " " + permAllies.toString() + " " + permOutside.toString());
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.edit(GlobalCity.displayName, "'" + displayName + "'");
    }

    public void setTag(String tag) {
        this.tag = tag;
        this.edit(GlobalCity.tag, "'" + tag + "'");
    }

    public void setRank(int rank) {
        this.rank = rank;
        this.edit(GlobalCity.rank, "'" + rank + "'");
    }

    public void setOwner(DBPlayer owner) {
        this.owner = owner;
        this.edit(GlobalCity.playerOwner, "'" + owner.getUser().getUniqueId().toString() + "'");
    }

    public void setPermRes(Permission permRes) {
        this.permRes = permRes;
        this.edit(GlobalCity.permRes, "'" + permRes.getId() + "'");
    }

    public void setPermAllies(Permission permAllies) {
        this.permAllies = permAllies;
        this.edit(GlobalCity.permAllies, "'" + permAllies.getId() + "'");
    }

    public void setPermOutside(Permission permOutside) {
        this.permOutside = permOutside;
        this.edit(GlobalCity.permOutside, "'" + permOutside.getId() + "'");
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

    public Boolean      contains(DBPlayer player) {
        return citizens.containsValue(player);
    }

    public void         addCitizen(DBPlayer player) {
        this.citizens.put(player.getDisplayName(), player);
    }

    public void         removeCitizen(DBPlayer player) {
        this.citizens.remove(player.getDisplayName());
    }

    public String   getAssistantsAsString() {
        String      message = "";
        Collection<DBPlayer> list = getAssistants();
        int         i = 0, max = list.size();

        for (DBPlayer p : list) {
            message += p.getDisplayName();
            if (i != max - 1)
                message += ", ";
        }
        return message;
    }

    public String   getCitizensAsString() {
        String      message = "";
        Collection<DBPlayer> list = getCitizens();
        int         i = 0, max = list.size();

        for (DBPlayer p : list) {
            message += p.getDisplayName();
            if (i != max - 1)
                message += ", ";
        }
        return message;
    }
}