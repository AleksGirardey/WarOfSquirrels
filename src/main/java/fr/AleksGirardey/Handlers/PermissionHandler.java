package fr.AleksGirardey.Handlers;

import com.google.inject.Inject;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Cuboide.Chunk;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.Permission;
import fr.AleksGirardey.Objects.Database.GlobalCity;
import fr.AleksGirardey.Objects.Database.GlobalPermission;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import fr.AleksGirardey.Objects.Database.Statement;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PermissionHandler {

    private Logger logger;
    private Map<Integer, Permission> permissionMap = new HashMap<>();

    @Inject
    public PermissionHandler(Logger logger) {
        this.logger = logger;
        this.populate();
    }

    private void        populate() {
        String          sql = "SELECT * FROM `" + GlobalPermission.tableName + "`;";
        Permission      permission;

        try {
            Statement   statement = new Statement(sql);
            statement.Execute();
            while (statement.getResult().next()) {
                permission = new Permission(statement.getResult());
                permissionMap.put(permission.getId(), permission);
            }
            statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Permission       add(boolean build, boolean container, boolean swi) {
        Permission          permission = new Permission(build, container, swi);
        this.permissionMap.put(permission.getId(), permission);

        return permission;
    }

    public void     delete(Permission perm) {
        this.permissionMap.remove(perm.getId());
    }

    public void     delete(int id) {
        this.permissionMap.remove(id);
    }

    public Permission     get(int id) { return permissionMap.get(id); }

/*
    public boolean          ableTo(Player player, int chunkId) {
        return false;
    }

    public boolean          ableTo(Player player, int chunkId, String perm) throws SQLException {
        PlayerHandler       plh = Core.getPlayerHandler();
        CityHandler         cityHandler = Core.getCityHandler();
        String              option = GlobalCity.permOutside;
        int                 cityPlayerId, permId, cityId;

        cityId = Core.getChunkHandler().getCity(chunkId);
        // PLAYER cherche à PERM sur le chunk CHUNKID

        // verification si Player possède une ville
        cityPlayerId = plh.<Integer>getElement(player, GlobalPlayer.cityId);
        if (cityPlayerId != 0) { // PLAYER possède une ville
            if (cityPlayerId == cityId) { // Si PLAYER est un citoyen
                if (cityHandler.<String>getElement(cityId, GlobalCity.playerOwner)
                        .equals(player.getUniqueId().toString()))
                    return true; // Owner => always OK
                option = GlobalCity.permRes;
            } else if (cityHandler.areAllies(cityId, cityPlayerId)) { // Villes alliés
                permId = cityHandler.getDiplomacy(cityId, true).get(cityPlayerId).getL();
                return getPerm(permId, "permission_" + perm);
            }
        }
        permId = cityHandler.<Integer>getElement(cityId, option);
        return getPerm(permId, "permission_" + perm);
    } */

    public String           toString(City city) {
        String              res = "";

        res += "R [";
        res += (city.getPermRes().toString());
        res += "] | A [";
        res += (city.getPermAllies().toString());
        res += "] | O [";
        res += (city.getPermOutside().toString());
        res += "]";

        return res;
    }

    private Logger getLogger() {
        return logger;
    }
}