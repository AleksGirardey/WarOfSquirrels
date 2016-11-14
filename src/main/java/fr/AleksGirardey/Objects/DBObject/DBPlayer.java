package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DBPlayer extends DBObject {
    private String          _primaryKeyName = GlobalPlayer.uuid;
    private String          _tableName = GlobalPlayer.tableName;
    private String          _fields = "`" + GlobalPlayer.displayName
            + "`, `" + GlobalPlayer.score
            + "`, `" + GlobalPlayer.cityId
            + "`, `" + GlobalPlayer.assistant + "`";
    private String          _newFields = "`" + GlobalPlayer.uuid + "`, " + _fields;

    private User            user;
    private String          displayName;
    private int             score;
    private City            city;
    private Boolean         assistant;

    public DBPlayer(Player player) {
        super();
        this.user = Core.getPlayerHandler().getUser(player.getUniqueId()).get();
        this.displayName = player.getName();
        this.score = 0;
        this.city = null;
        this.assistant = false;
        this._primaryKeyValue = player.getUniqueId().toString();
        this.add("`" + _primaryKeyValue + "`, `"
                + displayName + "`, `"
                + score + "`, `"
                + "NULL`, `FALSE`");
    }

    public DBPlayer(ResultSet rs) throws SQLException {
        this._primaryKeyValue = rs.getString(GlobalPlayer.uuid);
        this.user = Core.getPlayerHandler().getUser(
                UUID.fromString(rs.getString(GlobalPlayer.uuid))).get();
        this.displayName = rs.getString(GlobalPlayer.displayName);
        this.score = rs.getInt(GlobalPlayer.score);
        this.city = Core.getCityHandler().get(rs.getInt(GlobalPlayer.cityId));
        this.assistant = rs.getBoolean(GlobalPlayer.assistant);
    }

    @Override
    protected int   add(String values) {
        String      sql = "INSERT INTO `"+ _tableName + "` (" + _newFields + ") VALUES (" + values + ");";
        return this.update();
    }

    public User     getUser() { return user; }
    public void     setUser(User user) { this.user = user; }

    public String getDisplayName() { return displayName; }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.edit(GlobalPlayer.displayName, displayName);
    }

    public int getScore() { return score; }

    public void setScore(int score) {
        this.score = score;
        this.edit(GlobalPlayer.score, "" + score);
    }

    public City getCity() { return city; }

    public void setCity(City city) {
        this.city = city;
        this.edit(GlobalPlayer.cityId, "" + city.getId());
    }

    public Boolean isAssistant() { return assistant; }

    public void setAssistant(Boolean assistant) {
        this.assistant = assistant;
        this.edit(GlobalPlayer.assistant, assistant ? "TRUE" : "FALSE");
    }
}
