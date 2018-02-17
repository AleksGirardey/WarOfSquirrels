package fr.craftandconquest.warofsquirrels.objects.dbobject;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class DBPlayer extends DBObject {
    private String          _tableName = GlobalPlayer.tableName;
    private static String          _fields = "`" + GlobalPlayer.displayName
            + "`, `" + GlobalPlayer.score
            + "`, `" + GlobalPlayer.cityId
            + "`, `" + GlobalPlayer.assistant
            + "`, `" + GlobalPlayer.resident
            + "`, `" + GlobalPlayer.account + "`";
    private String          _newFields = "`" + GlobalPlayer.uuid + "`, " + _fields;

    /* -- DB Fields -- */
    private User            user;
    private String          displayName;
    private int             score;
    private City            city;
    private Boolean         assistant;
    private Boolean         resident;
    private int             balance;

    /* -- Extra Fields -- */
    private int             lastChunkX = 10000;
    private int             lastChunkZ = 10000;
    private World           lastWorld = null;
    private boolean         reincarnation;
    private int             cityId;
    private boolean         adminMode;
    private long            lastClick;

    public DBPlayer(Player player) {
        super(GlobalPlayer.uuid, GlobalPlayer.tableName, _fields);
        this.user = Core.getPlayerHandler().getUser(player.getUniqueId()).get();
        this.displayName = player.getName();
        this.score = 0;
        this.city = null;
        this.cityId = 0;
        this.balance = Core.getConfig().getStartBalance();
        this.assistant = false;
        this.resident = false;
        this.reincarnation = false;
        this.adminMode = false;
        this._primaryKeyValue = player.getUniqueId().toString();
        this.add("'" + _primaryKeyValue + "', '"
                + displayName + "', "
                + score + ", "
                + "NULL, FALSE, FALSE, " + balance);
        writeLog();
    }

    public DBPlayer(ResultSet rs) throws SQLException {
        super(GlobalPlayer.uuid, GlobalPlayer.tableName, _fields);
        this._primaryKeyValue = rs.getString(GlobalPlayer.uuid);
        this.user = Core.getPlayerHandler().getUser(
                UUID.fromString(rs.getString(GlobalPlayer.uuid))).get();
        this.displayName = rs.getString(GlobalPlayer.displayName);
        this.score = rs.getInt(GlobalPlayer.score);
        this.cityId = rs.getInt(GlobalPlayer.cityId);
        this.assistant = rs.getBoolean(GlobalPlayer.assistant);
        this.resident = rs.getBoolean(GlobalPlayer.resident);
        this.balance = rs.getInt(GlobalPlayer.account);
        this.reincarnation = false;
        this.lastClick = Instant.now().getEpochSecond();
        writeLog();
    }

    public DBPlayer(String uuid, String name) {
        super(GlobalPlayer.uuid, GlobalPlayer.tableName, _fields);
        this.user = null;
        this.displayName = name;
        this.score = 0;
        this.city = null;
        this.cityId = 0;
        this.balance = Core.getConfig().getStartBalance();
        this.assistant = false;
        this.resident = false;
        this.reincarnation = false;
        this.adminMode = false;
        this._primaryKeyValue = uuid;
        this.lastClick = Instant.now().getEpochSecond();
        this.add("'" + _primaryKeyValue + "', '"
                + displayName + "', "
                + score + ", "
                + "NULL, FALSE, FALSE, " + balance);
    }

    public void         updateDependencies() {
        if (cityId != 0) {
            this.city = Core.getCityHandler().get(cityId);
            Core.getLogger().info("[Updating] Player '" + this.displayName + "' is now in '" + this.city.getDisplayName() + "'\n" +
                    "Citizens size : " + city.getCitizens().size());
        }
    }

    public void     writeLog() {
        Core.getLogger().info("[Player] (" + _fields + ") : #" + _primaryKeyValue
                + "," + displayName
                + "," + score
                + "," + (city != null ? "'" + city.getDisplayName() + "'" : "null")
                + "," + balance);
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
            this.edit(GlobalPlayer.cityId, "NULL");
    }

    public Boolean      isAssistant() { return assistant; }
    public Boolean      isResident() { return resident; }

    public void         setAssistant(Boolean assistant) {
        this.assistant = assistant;
        this.edit(GlobalPlayer.assistant, assistant ? "TRUE" : "FALSE");
    }

    public void setResident(Boolean resident) {
        this.resident = resident;
        this.edit(GlobalPlayer.resident, resident ? "TRUE" : "FALSE");
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
    public World    getLastWorld() { return lastWorld; }
    public boolean  isInReincarnation() { return reincarnation; }

    public void     setAdminMode() { adminMode = !adminMode; }
    public boolean  hasAdminMode() { return adminMode; }

    public void     setLastChunkX(int pos) { lastChunkX = pos; }
    public void     setLastChunkZ(int pos) { lastChunkZ = pos; }
    public void     setLastWorld(World world) { lastWorld = world; }
    public void     setReincarnation(boolean reinca) {
        reincarnation = reinca;
    }

    /* -- Balance related -- */

    public void     insert(int money) {
        setBalance(balance += money);
    }

    public void     withdraw(int money) {
        setBalance((this.balance - money) < 0 ? 0 : this.balance - money);
    }

    private void     setBalance(int newBalance) {
        this.balance = newBalance;
        this.edit(GlobalPlayer.account, "" + this.balance);
    }

    public int getBalance() { return balance; }

    @Override
    public String       toString() { return this.displayName; }

    public long getLastClick() { return lastClick; }
    public void setLastClick(long time) { this.lastClick = time; }
    public boolean getElapsedTimeClick() {
        if (lastClick == 0)
            return true;

        return (Instant.now().getEpochSecond() - lastClick) > 1;
    }
}
