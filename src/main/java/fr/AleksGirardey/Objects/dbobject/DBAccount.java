package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalAccount;

import java.sql.ResultSet;
import java.sql.SQLException;

public class            DBAccount extends DBObject{
    private String      _tableName = GlobalAccount.tableName;
    private static String      _fields = "`" + GlobalAccount.po
            + "`"; /*, `" + GlobalAccount.pa
            + "`, `" + GlobalAccount.pb + "`"; */

    /* -- DBObject class related -- */
    private int         po;
/*    private int         pa;
    private int         pb;*/

    /* -- Extra Fields -- */


    /* -- Constructor --
    ** Using 3 int to create a new Account
    */
    public DBAccount(int _po/*, int _pa, int _pb*/) {
        super(GlobalAccount.id, GlobalAccount.tableName, _fields);

        this.po = _po;
//        this.pa = _pa;
//        this.pb = _pb;
        this._primaryKeyValue = "" + this.add("" + po /*+ "," + pa + "," + pb*/);
        writeLog();
    }

    /* -- Constructor --
    ** Using ResultSet to get from DB
    */
    public DBAccount(ResultSet rs) throws SQLException {
        super(GlobalAccount.id, GlobalAccount.tableName, _fields);

        this.po = rs.getInt(GlobalAccount.po);
//        this.pa = rs.getInt(GlobalAccount.pa);
//        this.pb = rs.getInt(GlobalAccount.pb);
        this._primaryKeyValue = rs.getString(GlobalAccount.id);
    }

    public int      getId() { return Integer.parseInt(this._primaryKeyValue); }

    public int      getPo() { return po; }

    public void     setPo(int po) {
        this.po = po;
        this.edit(GlobalAccount.po, "" + po);
    }
/*
    public int getPa() { return pa; }

    public void setPa(int pa) {
        this.pa = pa;
        this.edit(GlobalAccount.pa, "" + pa);
    }

    public int getPb() { return pb; }

    public void setPb(int pb) {
        this.pb = pb;
        this.edit(GlobalAccount.pb, "" + pb);
    }

    public void     setBalance(int po, int pa, int pb) {
        this.po = po;
        this.pa = pa;
        this.pb = pb;
        this._sql = "UPDATE `" + _tablename + "` SET "
                + "`" + _tablename + "`.`" + GlobalAccount.po + "` = " + po + " , "
                + "`" + _tablename + "`.`" + GlobalAccount.pa + "` = " + pa + " , "
                + "`" + _tablename + "`.`" + GlobalAccount.pb + "` = " + pb
                + " WHERE `" + _primaryKeyName + "` = '" + _primaryKeyValue + "';";

        this.update();
    } */

    @Override
    protected void writeLog() {
        Core.Send("[Account] " + _fields + " : #" + _primaryKeyValue + " , " + po /*+ " , " + pa + " , " + pb*/);
    }
}