package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.Vector3;

import java.util.List;
import java.util.Map;

public class PermissionHandler {

    public enum Rights {
        SET_HOMEBLOCK,
        BUILD,
        CONTAINER,
        SWITCH,
        FARM,
        INTERACT
    }

    public boolean hasRightsTo(Rights rights, Object... objects) {
        switch (rights) {
            case SET_HOMEBLOCK:
                return hasRightsToSetHomeBlock((Player) objects[0], (Chunk) objects[1]);
            case BUILD:
                return hasRightsToBuild((Vector3) objects[0], (int) objects[1], (Player) objects[2]);
            case CONTAINER:
                return hasRightsToContainer((Vector3) objects[0], (int) objects[1], (Player) objects[2]);
            case SWITCH:
                return hasRightsToSwitch((Vector3) objects[0], (int) objects[1], (Player) objects[2]);
            case FARM:
                return hasRightsToFarm((Vector3) objects[0], (int) objects[1], (Player) objects[2]);
            case INTERACT:
                return hasRightsToInteract((Vector3) objects[0], (int) objects[1], (Player) objects[2]);
            default:
                return false;
        }
    }

    private boolean hasRightsToBuild(Vector3 position, int dimensionId, Player player) {
        return getPermissionToCheck(position, dimensionId, player).build;
    }

    private boolean hasRightsToContainer(Vector3 position, int dimensionId, Player player) {
        return getPermissionToCheck(position, dimensionId, player).container;
    }

    private boolean hasRightsToSwitch(Vector3 position, int dimensionId, Player player) {
        return getPermissionToCheck(position, dimensionId, player).switches;
    }

    private boolean hasRightsToFarm(Vector3 position, int dimensionId, Player player) {
        return getPermissionToCheck(position, dimensionId, player).farm;
    }

    private boolean hasRightsToInteract(Vector3 position, int dimensionId, Player player) {
        return getPermissionToCheck(position, dimensionId, player).interact;
    }

    private boolean hasRightsToSetHomeBlock(Player player, Chunk chunk) {
        return false;
    }

    private Permission getPermissionToCheck(Vector3 position, int dimensionId, Player player) {
        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk((int) position.x, (int) position.z, dimensionId);
        Permission          permission;

        // If the block isn't in a claimed chunk, default nature permissions apply
        if (chunk == null) {
            return WarOfSquirrels.instance.getConfig().getDefaultNaturePermission();
        }

        // The chunk is claimed, we need to know how the player is related to the chunk (Citizen, ally, enemy, outsider)
        if (player.getCity() != null) {
            /*
             ** Player belongs to a city, we need to set if the chunk belongs to his city
             */
            if (player.getCity() == chunk.getCity()) {
                /*
                 ** The city chunk is the same as the player chunk, we have to define is status in the city
                 */
                if (player.getAssistant() || player.getCity().getOwner() == player)
                    return new Permission(true, true, true, true, true);
                else {
                    permission = player.getCity().getCustomPermission().getOrDefault(player, (player.getResident() ?
                            player.getCity().getDefaultPermission().get(PermissionRelation.RESIDENT) :
                            player.getCity().getDefaultPermission().get(PermissionRelation.RECRUIT)));

                    /*
                     ** Le joueur n'a aucun rang qui outre-passe les droits d'un eventuel
                     ** cubo, on verifie donc si le block appartient à un cubo
                     */
                    Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(position);
                    if (cubo != null) {
                        /*
                         ** On vérifie si le joueur est dans la liste ou l'owner
                         */
                        List<Player> inList = cubo.getInList();
                        if (inList.contains(player) || cubo.getOwner() == player)
                            permission = cubo.getPermissionIn();
                        else
                            permission = cubo.getPermissionOut();
                    }
                }
            } else {
                /*
                 ** Le joueur n'appartient pas à la ville il faut donc verifié si il est
                 ** allié, enemi, ou de la même faction
                 */
                boolean isAlly = WarOfSquirrels.instance.getDiplomacyHandler()
                        .getAllies(chunk.getCity().getFaction()).contains(player.getCity().getFaction());
                boolean isEnemy = WarOfSquirrels.instance.getDiplomacyHandler()
                        .getEnemies(chunk.getCity().getFaction()).contains(player.getCity().getFaction());

                boolean isFaction = chunk.getCity().getFaction().equals(player.getCity().getFaction());

                Map<IPermission, Permission> customPerm = chunk.getCity().getCustomPermission();

                permission = customPerm.getOrDefault(player,
                        customPerm.getOrDefault(player.getCity(),
                                customPerm.getOrDefault(player.getCity().getFaction(),
                                        chunk.getCity().getDefaultPermission().get(isAlly ?
                                                PermissionRelation.ALLY : (isEnemy ?
                                                PermissionRelation.ENEMY : isFaction ?
                                                PermissionRelation.FACTION : PermissionRelation.OUTSIDER)))));
            }
        } else  // else Outsider
            permission = chunk.getCity().getCustomPermission().getOrDefault(player,
                    chunk.getCity().getDefaultPermission().get(PermissionRelation.OUTSIDER));

        return permission;
    }
}
