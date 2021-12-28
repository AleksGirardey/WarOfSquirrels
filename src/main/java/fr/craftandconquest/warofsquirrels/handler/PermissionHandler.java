package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.ChunkLocation;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.OnSaveListener;
import fr.craftandconquest.warofsquirrels.utils.Pair;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionHandler {

    public enum Rights {
        SET_HOMEBLOCK,
        BUILD,
        CONTAINER,
        SWITCH,
        FARM,
        INTERACT,
        PLACE_IN_WAR,
        DESTROY_IN_WAR
    }

    private static final List<Block> authorizedPlacedItems = new ArrayList<>();
    private static final List<Block> authorizedDestroyedItems = new ArrayList<>();

    static {
        authorizedPlacedItems.add(Blocks.LADDER);

        authorizedDestroyedItems.add(Blocks.GLASS);
        authorizedDestroyedItems.add(Blocks.GLASS_PANE);
    }

    public boolean hasRightsTo(Rights rights, Object... objects) {
        ResourceKey<Level> dimension = Chunk.IdToDimension((int) objects[1]);
        switch (rights) {
            case SET_HOMEBLOCK:
                return hasRightsToSetHomeBlock((FullPlayer) objects[0], (Chunk) objects[1]);
            case PLACE_IN_WAR:
                return hasRightsToPlaceInWar((Vector3) objects[0], dimension, (FullPlayer) objects[2], (Block) objects[3]);
            case DESTROY_IN_WAR:
                return hasRightsToDestroyInWar((Vector3) objects[0], dimension, (FullPlayer) objects[2], (Block) objects[3]);
            case BUILD:
                return hasRightsToBuild((Vector3) objects[0], dimension, (FullPlayer) objects[2]);
            case CONTAINER:
                return hasRightsToContainer((Vector3) objects[0], dimension, (FullPlayer) objects[2]);
            case SWITCH:
                return hasRightsToSwitch((Vector3) objects[0], dimension, (FullPlayer) objects[2]);
            case FARM:
                return hasRightsToFarm((Vector3) objects[0], dimension, (FullPlayer) objects[2]);
            case INTERACT:
                return hasRightsToInteract((Vector3) objects[0], dimension, (FullPlayer) objects[2]);
            default:
                return false;
        }
    }

    private boolean hasRightsToBuild(Vector3 position, ResourceKey<Level> dimensionId, FullPlayer player) {
        return getPermissionToCheck(position, dimensionId, player).build;
    }

    private boolean hasRightsToContainer(Vector3 position, ResourceKey<Level> dimensionId, FullPlayer player) {
        return getPermissionToCheck(position, dimensionId, player).container;
    }

    private boolean hasRightsToSwitch(Vector3 position, ResourceKey<Level> dimensionId, FullPlayer player) {
        return getPermissionToCheck(position, dimensionId, player).switches;
    }

    private boolean hasRightsToFarm(Vector3 position, ResourceKey<Level> dimensionId, FullPlayer player) {
        return getPermissionToCheck(position, dimensionId, player).farm;
    }

    private boolean hasRightsToInteract(Vector3 position, ResourceKey<Level> dimensionId, FullPlayer player) {
        return getPermissionToCheck(position, dimensionId, player).interact;
    }

    private boolean hasRightsToSetHomeBlock(FullPlayer player, Chunk chunk) {
        return false;
    }

    private boolean hasRightsToPlaceInWar(Vector3 position, ResourceKey<Level> dimensionId, FullPlayer player, Block block) {
        if (authorizedPlacedItems.contains(block))
            return getPermissionToCheck(position, dimensionId, player).build;
        return false;
    }

    private boolean hasRightsToDestroyInWar(Vector3 position, ResourceKey<Level> dimensionId, FullPlayer player, Block block) {
        if (authorizedDestroyedItems.contains(block))
            return getPermissionToCheck(position, dimensionId, player).build;
        return false;
    }

    private Permission checkCuboPermission(FullPlayer player, Cubo cubo) {
        List<FullPlayer> inList = cubo.getInList();
        if (inList.contains(player) || cubo.getOwner().equals(player))
            return cubo.getPermissionIn();
        else if (cubo.getCustomPermission(player) != null)
            return cubo.getCustomPermission(player);
        else
            return cubo.getPermissionOut();
    }

    private Permission checkChunkPermission(FullPlayer player, Chunk chunk) {
        boolean hasCity = player.getCity() != null;
        boolean isOwner = hasCity && player.getCity().getOwner().equals(player);
        boolean isAssistant = player.getAssistant();
        boolean isResident = player.getResident();


    }

    private Permission getPermissionToCheck(Vector3 position, ResourceKey<Level> dimensionId, FullPlayer player) {
        Permission permission;

        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(position);
        boolean isThereCubo = cubo != null;

        Pair<Integer, Integer> chunkLocation = Utils.WorldToChunkCoordinates((int) position.x, (int) position.z);
        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(chunkLocation.getKey(), chunkLocation.getValue(), dimensionId);
        boolean isThereChunk = chunk != null;

        Pair<Integer, Integer> territoryLocation = Utils.ChunkToTerritoryCoordinates(chunkLocation.getKey(), chunkLocation.getValue());
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(territoryLocation.getKey(), territoryLocation.getValue());
        boolean isThereTerritory = territory != null; // ALWAYS TRUE IF OVERWORLD



        if (isThereCubo) return checkCuboPermission(player, cubo);
        else if (isThereChunk) return checkChunkPermission(player, chunk);



        // If the block isn't in a claimed chunk, territory or default nature permissions apply
        if (chunk == null) {
            if (territory == null || territory.getFaction() == null)
                return WarOfSquirrels.instance.getConfig().getDefaultNaturePermission();

            permission = ExtractCustomPermission(territory.getFaction().getCustomPermission());

            if (permission == null) return
        }

        // The chunk is claimed, we need to know how the player is related to the chunk (Citizen, ally, enemy, outsider)
        if (player.getCity() != null) {
            /*
             ** Player belongs to a city, we need to set if the chunk belongs to his city
             */
            if (player.getCity() == chunk.getCity()) {
                // Verification dans un premier temps si le chunk et le joueur est en guerre

                if (WarOfSquirrels.instance.getWarHandler().Contains(chunk.getCity())) {
                    War war = WarOfSquirrels.instance.getWarHandler().getWar(chunk.getCity());

                    if (war.getCityDefender() == chunk.getCity() && war.contains(player)) {
                        return new Permission(true, true, true, false, false);
                    }
                }

                /*
                 ** The city chunk is the same as the player chunk, we have to define his status in the city
                 */
                if (player.getAssistant() || player.getCity().getOwner().equals(player)) {
                    return new Permission(true, true, true, true, true);
                } else {
                    permission = player.getCity().getCustomPermission().getOrDefault(player, (player.getResident() ?
                            player.getCity().getDefaultPermission().get(PermissionRelation.RESIDENT) :
                            player.getCity().getDefaultPermission().get(PermissionRelation.RECRUIT)));
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

                if (WarOfSquirrels.instance.getWarHandler().Contains(chunk.getCity())) {
                    War war = WarOfSquirrels.instance.getWarHandler().getWar(chunk.getCity());

                    if (war.contains(player)) {
                        if (isAlly || isFaction)
                            permission = new Permission(true, true, true, false, false);
                        else if (isEnemy)
                            permission = new Permission(true, false, false, false, false);
                    } else {
                        permission = new Permission(false, false, false, false, false);
                    }
                }
            }
        } else { // else Outsider
            permission = chunk.getCity().getCustomPermission().getOrDefault(player,
                    chunk.getCity().getDefaultPermission().get(PermissionRelation.OUTSIDER));
        }




        return permission;
    }
}
