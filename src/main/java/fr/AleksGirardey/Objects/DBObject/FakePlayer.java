package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalFakePlayer;

public class                FakePlayer extends DBPlayer {
    private String          tableName = "FakePlayer";
    private String          fields = GlobalFakePlayer.id + ", "
            + GlobalFakePlayer.posX + ", "
            + GlobalFakePlayer.posY + ", "
            + GlobalFakePlayer.posZ + ", ";

    private String      primaryKeyValue;
    private int         posX;
    private int         posY;
    private int         posZ;

    public FakePlayer(String name, int x, int y, int z) {
        super(name);

        this._primaryKeyValue = name;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    @Override
    public void     delete() {
        ((DBPlayer)this).delete();
        _sql = "DELETE FROM `" + _tablename + "` WHERE `" + GlobalFakePlayer.id + "` = ``";
        this.update();
    }

    @Override
    public String   add(String values) {
        _sql = "INSERT INTO `" + _tablename + "` (" + fields + ") VALUES (`" + values + "`);";
        return this.update();
    }

    @Override
    public void     edit(String field, String value) {
        _sql = "UPDATE `" + tableName + "` SET `" + tableName + "`.`" + field + "` = " + value
                + " WHERE `" + GlobalFakePlayer.id + "` = " + this.primaryKeyValue;
        Core.getLogger().info("[DB] Editing " + tableName + " at id '" + this.primaryKeyValue + "' :" +
                " '" + field + "' => '" + value + "'");
        this.update();
    }
}
