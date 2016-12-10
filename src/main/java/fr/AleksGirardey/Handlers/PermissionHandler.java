package fr.AleksGirardey.Handlers;

import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Cubo;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Permission;
import fr.AleksGirardey.Objects.Database.GlobalPermission;
import fr.AleksGirardey.Objects.Database.Statement;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionHandler {

    private Logger logger;
    private Map<Integer, Permission> permissionMap = new HashMap<>();

    @Inject
    public PermissionHandler(Logger logger) {
        this.logger = logger;
    }

    public void        populate() {
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

    public void             delete(Permission perm) {
        this.permissionMap.remove(perm.getId());
        perm.delete();
    }

    public void             delete(int id) {
        this.permissionMap.remove(id);
    }

    public Permission       get(int id) { return permissionMap.get(id); }

    public boolean          ableTo(DBPlayer player, Chunk chunk, String perm, Vector3i block) {
        Permission          permission;

        // Situer le joueur par rapport au chunk (résident, allié, outside)
        if (player.getCity() != null) {
            /*
            ** Le joueur possède une ville, il faut maintenant savoir si il interagit avec
            ** sa ville ou non.
            */
            if (player.getCity() == chunk.getCity()) {
                /*
                ** Le joueur appartient à la ville du chunk; on identifie alors si il est
                ** owner / assistant
                */
                if (player.isAssistant() || player.getCity().getOwner() == player)
                    return true;
                else {
                    /*
                    ** Le joueur n'a aucun rang qui outre-passe les droits d'un eventuel
                    ** cubo, on verifie donc si le block appartient à un cubo
                    */
                    Cubo cubo = Core.getCuboHandler().get(block);
                    if (cubo != null) {
                        /*
                        ** On vérifie si le joueur est dans la liste ou l'owner
                        */
                        List<DBPlayer> inList = cubo.getInList();
                        if (inList.contains(player) || cubo.getOwner() == player)
                            permission = cubo.getPermissionIn();
                        else
                            permission = cubo.getPermissionOut();
                    } else
                        permission = player.getCity().getPermRes();
                }
            } else {
                /*
                ** Le joueur n'appartient pas à la ville il faut donc verifié si il est
                ** allié ou enemi
                */
                if (Core.getDiplomacyHandler().getAllies(chunk.getCity()).contains(player.getCity())) {
                    /* Ally */
                    permission = chunk.getCity().getPermAllies();
                } else {
                    /* Enemy or Neutral */
                    permission = chunk.getCity().getPermOutside();
                }
            }
        } // else Outsider
        permission = chunk.getCity().getPermOutside();

        return (perm.equals("Build") ?
                permission.getBuild() :
                (perm.equals("Container") ?
                        permission.getContainer() :
                        permission.getSwitch()));
    }

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