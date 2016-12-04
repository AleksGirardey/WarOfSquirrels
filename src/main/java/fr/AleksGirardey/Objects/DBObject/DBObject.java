package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.Statement;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;

public abstract class DBObject {

    protected String        _sql;
    protected String        _primaryKeyName;
    protected String        _primaryKeyValue;
    protected String        _tablename;
    protected String        _fields;

    public DBObject(String primaryKeyName, String tablename, String fields) {
        this._primaryKeyName = primaryKeyName;
        this._tablename = tablename;
        this._fields = fields;
    }

    public void      delete() {
        _sql = "DELETE FROM `" + _tablename + "` WHERE `" + _primaryKeyName + "` = " + _primaryKeyValue;
        this.update();
    }

    protected void      edit(String field, String value) {
        _sql = "UPDATE `" + _tablename + "` SET `" + _tablename + "`.`" + field + "` = "
                + value + " WHERE `" + _primaryKeyName + "` = '" + _primaryKeyValue + "';";
        this.update();
    }

    protected String      add(String values) {
        _sql = "INSERT INTO `" + _tablename + "` (" + _fields + ") VALUES (" + values + ");";
        this._primaryKeyValue = this.update();
        Core.getLogger().info("[DB] Adding on '" + _tablename + "' : '" + _primaryKeyValue + "'");
        return _primaryKeyValue;
    }

    protected abstract void         writeLog();

    protected String    update() {
        Statement       _statement;
        String             id = "";

        Core.getLogger().info("Updating : >> " + _sql + " <<");

        try {
            _statement = new Statement(_sql);
            _statement.Update();
            if (_statement.getKeys().next())
                id = _statement.getKeys().getString(1);
            else
                Core.getLogger().info("[ERROR] NO KEY GENERATED");
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void set_primaryKeyName(String _primaryKeyName) { this._primaryKeyName = _primaryKeyName; }

    public void set_primaryKeyValue(String _primaryKeyValue) { this._primaryKeyValue = _primaryKeyValue; }

    public void set_tablename(String _tablename) { this._tablename = _tablename; }
}