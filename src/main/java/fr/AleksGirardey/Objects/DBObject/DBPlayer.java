package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DBPlayer extends DBObject {
    private String          _tableName = GlobalPlayer.tableName;
    private String          _fields = "`" + GlobalPlayer.displayName
            + "`, `" + GlobalPlayer.score
            + "`, `" + GlobalPlayer.cityId
            + "`, `" + GlobalPlayer.assistant + "`";
    private String          _newFields = "`" + GlobalPlayer.uuid + "`, " + _fields;

    /* -- DB Fields -- */
    private User            user;
    private String          displayName;
    private int             score;
    private City            city;
    private Boolean         assistant;

    /* -- Extra Fields -- */
    private int             lastChunkX;
    private int             lastChunkZ;

    private int             cityId;

    public DBPlayer(Player player) {
        super(GlobalPlayer.uuid, GlobalPlayer.tableName, "`" + GlobalPlayer.displayName
                + "`, `" + GlobalPlayer.score
                + "`, `" + GlobalPlayer.cityId
                + "`, `" + GlobalPlayer.assistant + "`");
        this.user = Core.getPlayerHandler().getUser(player.getUniqueId()).get();
        this.displayName = player.getName();
        this.score = 0;
        this.city = null;
        this.cityId = 0;
        this.assistant = false;
        this._primaryKeyValue = player.getUniqueId().toString();
        this.add("'" + _primaryKeyValue + "', '"
                + displayName + "', '"
                + score + "', "
                + "NULL, FALSE");
        writeLog();
    }

    public DBPlayer(ResultSet rs) throws SQLException {
        super(GlobalPlayer.uuid, GlobalPlayer.tableName, "`" + GlobalPlayer.displayName
                + "`, `" + GlobalPlayer.score
                + "`, `" + GlobalPlayer.cityId
                + "`, `" + GlobalPlayer.assistant + "`");
        this._primaryKeyValue = rs.getString(GlobalPlayer.uuid);
        this.user = Core.getPlayerHandler().getUser(
                UUID.fromString(rs.getString(GlobalPlayer.uuid))).get();
        this.displayName = rs.getString(GlobalPlayer.displayName);
        this.score = rs.getInt(GlobalPlayer.score);
        this.cityId = rs.getInt(GlobalPlayer.cityId);
        this.assistant = rs.getBoolean(GlobalPlayer.assistant);
        writeLog();
    }

    public void         updateDependencies() {
        if (cityId != 0) {
            this.city = Core.getCityHandler().get(cityId);
            Core.getLogger().info("[Updating] Player '" + this.displayName + "' is now in '" + this.city.getDisplayName() + "'");
        }
    }

    public void     writeLog() {
        Core.getLogger().info("[Creation] Player '" + displayName + "' has a score of " + score + ". He belongs to "
                + (city != null ? "'" + city.getDisplayName() + "'" : "no one."));
    }

    @Override
    protected String   add(String values) {
        this._sql = "INSERT INTO `"+ _tableName +"` (" + _newFields + ") VALUES (" + values + ");";
        Core.getLogger().info("SQL : << " + _sql + " >>");
        return this.update();
    }

    public String   getId() { return this._primaryKeyValue; }

    public User     getUser() { return user; }
    public void     setUser(User user) { this.user = user; }

    public String getDisplayName() { return displayName; }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.edit(GlobalPlayer.displayName, "'" + displayName + "'");
    }

    public int getScore() { return score; }

    public void setScore(int score) {
        this.score = score;
        this.edit(GlobalPlayer.score, "" + score);
    }

    public City getCity() { return city; }

    public void setCity(City city) {
        this.city = city;
        if (city != null)
            this.edit(GlobalPlayer.cityId, "'" + city.getId() + "'");
        else
            this.edit(GlobalPlayer.cityId, "'0'");
    }

    public Boolean      isAssistant() { return assistant; }

    public void         setAssistant(Boolean assistant) {
        this.assistant = assistant;
        this.edit(GlobalPlayer.assistant, assistant ? "TRUE" : "FALSE");
    }

    public void sendMessage(Text message) {
        if (this.user.isOnline())
            user.getPlayer().get().sendMessage(message);
    }

    public int      getPosX() { return user.getPlayer().get().getLocation().getBlockX(); }
    public int      getPosY() { return user.getPlayer().get().getLocation().getBlockY(); }
    public int      getPosZ() { return user.getPlayer().get().getLocation().getBlockZ(); }

    public int      getLastChunkX() { return lastChunkX; }
    public int      getLastChunkZ() { return lastChunkZ; }

    public void     setLastChunkX(int pos) { lastChunkX = pos; }
    public void     setLastChunkZ(int pos) { lastChunkZ = pos; }
}
