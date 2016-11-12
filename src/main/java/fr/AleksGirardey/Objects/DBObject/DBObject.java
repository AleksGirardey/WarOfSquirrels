package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.Statement;

import java.sql.SQLException;

public abstract class DBObject {

    protected String        _sql;
    protected String        _primaryKeyName;
    protected String        _primaryKeyValue;
    protected String        _tablename;
    protected String        _fields;

    protected void      delete() {
        _sql = "DELETE FROM `" + _tablename + "` WHERE `" + _primaryKeyName + "` = " + _primaryKeyValue;
        this.update();
    }

    protected void      edit(String field, String value) {
        _sql = "UPDATE `" + _tablename + "` SET `" + _tablename + "`.`" + field + "` = `"
                + value + "` WHERE `" + _primaryKeyName + "` = `" + _primaryKeyValue + "`;";
        this.update();
    }

    protected int      add(String values) {
        _sql = "INSERT INTO `" + _tablename + "` (" + _fields + ") VALUES (" + values + ");";
        return this.update();
    }

    protected int       update() {
        Statement _statement;
        int             id = 0;

        try {
            _statement = new Statement(_sql);
            _statement.Update();
            id = _statement.getKeys().getInt(1);
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
}