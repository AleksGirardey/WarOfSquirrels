package fr.craftandconquest.handlers;

import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;
import fr.craftandconquest.objects.Core;
import fr.craftandconquest.objects.dbobject.*;
import fr.craftandconquest.objects.database.GlobalPermission;
import fr.craftandconquest.objects.database.Statement;
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

    public boolean          ableToInList(DBPlayer player, Cubo cubo, String perm, Vector3i block) {
        Permission          permission = cubo.getPermissionIn();

        return ableCubo(perm, permission);
    }

    public boolean          ableToOutList(DBPlayer player, Cubo cubo, String perm, Vector3i block) {
        Permission          permission = cubo.getPermissionOut();

        return ableCubo(perm, permission);
    }

    private boolean ableCubo(String perm, Permission permission) {
        switch (perm) {
            case "Build" : return permission.getBuild();
            case "Switch" : return permission.getSwitch();
            case "Container" : return permission.getContainer();
            default: return false;
        }
    }

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
                    permission = (player.isResident() ? player.getCity().getPermRes() : player.getCity().getPermRec());

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
                    }
                }
            } else {
                /*
                ** Le joueur n'appartient pas à la ville il faut donc verifié si il est
                ** allié ou enemi
                */
                if (Core.getDiplomacyHandler().getAllies(chunk.getCity().getFaction()).contains(player.getCity().getFaction())) {
                    /* Ally */
                    permission = chunk.getCity().getPermAllies();
                } else {
                    /* Enemy or Neutral */
                    permission = chunk.getCity().getPermOutside();
                }
            }
        } else  // else Outsider
            permission = chunk.getCity().getPermOutside();

        return (perm.equals("Build") ?
                permission.getBuild() :
                (perm.equals("Container") ?
                        permission.getContainer() :
                        permission.getSwitch()));
    }

    public String           toString(City city) {
        String              res = "";

        res += "R " + (city.getPermRec().toString());
        res += " | C " + (city.getPermRes().toString());
        res += " | A " + (city.getPermAllies().toString());
        res += " | O " + (city.getPermOutside().toString());

        return res;
    }

    private Logger getLogger() {
        return logger;
    }
}