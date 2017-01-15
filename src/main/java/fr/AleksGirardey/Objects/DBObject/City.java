package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalCity;
import fr.AleksGirardey.Objects.Database.GlobalFaction;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import fr.AleksGirardey.Objects.Database.Statement;

import javax.xml.ws.FaultAction;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class City extends DBObject {
    private static String       _fields = "`" + GlobalCity.displayName
            + "`, `" + GlobalCity.tag
            + "`, `" + GlobalCity.rank
            + "`, `" + GlobalCity.playerOwner
            + "`, `" + GlobalCity.faction
            + "`, `" + GlobalCity.permRec
            + "`, `" + GlobalCity.permRes
            + "`, `" + GlobalCity.permAllies
            + "`, `" + GlobalCity.permOutside
            + "`, `" + GlobalCity.permFaction
            + "`, `" + GlobalCity.account + "`";

    /* DB Fields */

    private String      displayName;
    private String      tag;
    private int         rank;
    private DBPlayer    owner;
    private Faction     faction;
    private Permission  permRec;
    private Permission  permRes;
    private Permission  permAllies;
    private Permission  permOutside;
    private Permission  permFaction;
    private int         balance;

    /* Extra Fields */
    private Map<String, DBPlayer>   citizens;

    public City(String _displayName, DBPlayer _owner, Faction _faction, Permission _rec,
                Permission _res, Permission _allies, Permission _outside, Permission _pFaction) {
        super(GlobalCity.id, GlobalCity.tableName, _fields);

        this.citizens = new HashMap<>();
        displayName = _displayName;
        tag = (displayName.length() <= 5 ? displayName : displayName.substring(0, 5));
        rank = 0;
        owner = _owner;
        faction = _faction;
        permRec = _rec;
        permRes = _res;
        permAllies = _allies;
        permOutside = _outside;
        permFaction = _pFaction;
        balance = 0;
        this.add("'" + displayName + "', '"
                + tag + "'," +
                "'0', '"
                + owner.getUser().getUniqueId().toString() + "', '"
                + faction.getId() + "', '"
                + permRec.getId() + "', '"
                + permRes.getId() + "', '"
                + permAllies.getId() + "', '"
                + permOutside.getId()+ "', '"
                + permFaction.getId() + "', "
                + balance);
        writeLog();
    }

    public City(ResultSet rs) throws SQLException {
        super(GlobalCity.id, GlobalCity.tableName, _fields);

        this.citizens = new HashMap<>();
        _primaryKeyValue = "" + rs.getInt(GlobalCity.id);
        displayName = rs.getString(GlobalCity.displayName);
        tag = rs.getString(GlobalCity.tag);
        rank = rs.getInt(GlobalCity.rank);
        owner = Core.getPlayerHandler().get(rs.getString(GlobalCity.playerOwner));
        faction = Core.getFactionHandler().get(rs.getInt(GlobalCity.faction));
        permRec = Core.getPermissionHandler().get(rs.getInt(GlobalCity.permRec));
        permRes = Core.getPermissionHandler().get(rs.getInt(GlobalCity.permRes));
        permAllies = Core.getPermissionHandler().get(rs.getInt(GlobalCity.permAllies));
        permOutside = Core.getPermissionHandler().get(rs.getInt(GlobalCity.permOutside));
        permFaction = Core.getPermissionHandler().get(rs.getInt(GlobalCity.permFaction));
        balance = rs.getInt(GlobalCity.account);

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
        Core.getLogger().info("[City] (" + _fields + ") : #" + _primaryKeyValue
                + "," + displayName
                + "," + tag
                + "," + rank
                + "," + owner.getDisplayName()
                + "," + faction.getDisplayName()
                + "," + permRec
                + "," + permRes
                + "," + permAllies
                + "," + permOutside
                + "," + permFaction
                + "," + balance);
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

    public void setFaction(Faction faction) {
        this.faction = faction;
        this.edit(GlobalCity.faction, "'" + faction.getId() + "'");
    }

    public void setPermRec(Permission permRec) {
        this.permRec = permRec;
        this.edit(GlobalCity.permRec, permRec != null ? ("'" + permRec.getId() + "'") : "NULL");
    }

    public void setPermRes(Permission permRes) {
        this.permRes = permRes;
        if (permRes != null)
            this.edit(GlobalCity.permRes, "'" + permRes.getId() + "'");
        else
            this.edit(GlobalCity.permRes, "NULL");
    }

    public void setPermAllies(Permission permAllies) {
        this.permAllies = permAllies;
        if (permAllies != null)
            this.edit(GlobalCity.permAllies, "'" + permAllies.getId() + "'");
        else
            this.edit(GlobalCity.permAllies, "NULL");
    }

    public void setPermOutside(Permission permOutside) {
        this.permOutside = permOutside;
        if (permOutside != null)
            this.edit(GlobalCity.permOutside, "'" + permOutside.getId() + "'");
        else
            this.edit(GlobalCity.permOutside, "NULL");
    }

    public void setPermFaction(Permission permFaction) {
        this.permFaction = permFaction;
        this.edit(GlobalCity.permFaction, permFaction == null ? "NULL" : ("'" + permFaction.getId() + "'"));
    }

    public int          getId() {
        return Integer.parseInt(_primaryKeyValue);
    }

    public String       getDisplayName() {
        return displayName;
    }

    public String       getTag() {
        return tag;
    }

    public int          getRank() {
        return rank;
    }

    public DBPlayer     getOwner() {
        return owner;
    }

    public Faction      getFaction() { return faction; }

    public Permission   getPermRec() { return permRec; }
    public Permission   getPermRes() {
        return permRes;
    }

    public Permission   getPermAllies() {
        return permAllies;
    }
    public Permission   getPermOutside() {
        return permOutside;
    }

    public Permission   getPermFaction() { return permFaction; }

    public Collection<DBPlayer> getRecruits() { return citizens.values().stream().filter(c -> (!c.isAssistant() && !c.getCity().getOwner().equals(c) && !c.isResident())).collect(Collectors.toList()); }
    public Collection<DBPlayer> getResidents() { return citizens.values().stream().filter(r -> (!r.isAssistant() && !r.getCity().getOwner().equals(r) && r.isResident())).collect(Collectors.toList());}
    public Collection<DBPlayer> getCitizens() { return citizens.values(); }

    public Collection<DBPlayer> getAssistants() {
        return getCitizens().stream().filter(DBPlayer::isAssistant).collect(Collectors.toCollection(ArrayList::new));
    }

    public Boolean      contains(DBPlayer player) {
        return citizens.containsValue(player);
    }

    public void         addCitizen(DBPlayer player) { this.citizens.put(player.getDisplayName(), player); }
    public void         removeCitizen(DBPlayer player) {
        this.citizens.remove(player.getDisplayName());
    }

    public String       getAssistantsAsString() {
        String          message = "";
        Collection<DBPlayer> list = getAssistants();
        int         i = 0, max = list.size();

        for (DBPlayer p : list) {
            message += p.getDisplayName();
            if (i != max - 1)
                message += ", ";
        }
        return message;
    }

    public String       getRecruitsInfo() {
        Collection<DBPlayer>    list = getRecruits();
        String      message = "";
        int         i = 0, max;

        max = list.size();
        for (DBPlayer p : list) {
            message += p.getDisplayName();
            if (i != max - 1)
                message += ", ";
        }
        return message;
    }

    public String   getResidentsInfo() {
        Collection<DBPlayer>    list = getResidents();
        int         i = 0, max = list.size();
        String      message = "";

        for (DBPlayer c : list) {
            message += c.getDisplayName();
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

    /* Balance */

    private void     setBalance(int balance) {
        this.balance = balance;
        this.edit(GlobalCity.account, "" + balance);
    }

    public void     insert(int money) {
        this.setBalance(balance + money);
    }

    public void     withdraw(int money) {
        this.setBalance((balance - money) < 0 ? 0 : balance - money);
    }

    public int      getBalance() { return balance; }
}