package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Database.GlobalPermission;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Permission extends DBObject{
    private String          _primaryKeyName = GlobalPermission.id;
    private String          _tableName = GlobalPermission.tableName;
    private String          _fields = "`" + GlobalPermission.build
            + "`, `" + GlobalPermission.container
            + "`, `" + GlobalPermission.switch_ + "`";

    private Boolean         build;
    private Boolean         container;
    private Boolean         switch_;

    public Permission(Boolean _build, Boolean _container, Boolean _switch) {
        build = _build;
        container = _container;
        switch_ = _switch;
        this._primaryKeyValue = "" + this.add("`"
                + (build ? "TRUE" : "FALSE")
                + (container ? "TRUE" : "FALSE")
                + (switch_ ? "TRUE" : "FALSE") + "`");
    }

    public Permission(ResultSet rs) throws SQLException {
        build = rs.getBoolean(GlobalPermission.build);
        container = rs.getBoolean(GlobalPermission.container);
        switch_ = rs.getBoolean(GlobalPermission.switch_);
    }

    @Override
    public String       toString() {
        String          res = "";

        res += (this.build ? "B" : "-");
        res += (this.container ? "C" : "-");
        res += (this.switch_ ? "S" : "-");

        return res;
    }

    public void setBuild(Boolean build) {
        this.build = build;
        this.edit(GlobalPermission.build, (build ? "TRUE" : "FALSE"));
    }

    public void setContainer(Boolean container) {
        this.container = container;
        this.edit(GlobalPermission.container, (container ? "TRUE" : "FALSE"));
    }

    public void setSwitch_(Boolean switch_) {
        this.switch_ = switch_;
        this.edit(GlobalPermission.switch_, (switch_ ? "TRUE" : "FALSE"));
    }

    public int      getId() {
        return Integer.parseInt(_primaryKeyValue);
    }

    public Boolean getBuild() {
        return build;
    }

    public Boolean getContainer() {
        return container;
    }

    public Boolean getSwitch() {
        return switch_;
    }
}