package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.permission.CustomPermission;
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

    private Permission extractCustomPermission(IPermission target, List<CustomPermission> permissionList) {
        for (CustomPermission custom : permissionList) {
            if (custom.targetUuid.equals(target.getUuid()))
                return custom.permission;
        }
        return null;
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
        boolean hasFaction = hasCity && player.getCity().getFaction() != null;
        boolean chunkHasFaction = chunk.getCity().getFaction() != null;
        boolean sameCity = hasCity && chunk.getCity().equals(player.getCity());
        boolean isOwner = hasCity && player.getCity().getOwner().equals(player);
        boolean isAssistant = player.getAssistant();
        boolean isResident = player.getResident();

        Permission customPerm;

        customPerm = extractCustomPermission(player, chunk.getCity().getCustomPermissionList());

        if (customPerm != null) return customPerm;

        if (!hasCity) {
            return chunk.getCity().getDefaultPermission().get(PermissionRelation.OUTSIDER);
        }

        customPerm = extractCustomPermission(player.getCity(), chunk.getCity().getCustomPermissionList());
        if (customPerm != null) return customPerm;

        if (player.getCity().getFaction() != null) {
            customPerm = extractCustomPermission(player.getCity().getFaction(), chunk.getCity().getCustomPermissionList());
            if (customPerm != null) return customPerm;
        }

        if (sameCity) {
            if (isOwner || isAssistant) return new Permission(true, true, true, true, true);

            return isResident ? chunk.getCity().getDefaultPermission().get(PermissionRelation.RESIDENT) :
                    chunk.getCity().getDefaultPermission().get(PermissionRelation.RECRUIT);
        }

        if (!chunkHasFaction || !hasFaction) return chunk.getCity().getDefaultPermission().get(PermissionRelation.OUTSIDER);

        boolean isAlly = WarOfSquirrels.instance.getDiplomacyHandler()
                .getAllies(chunk.getCity().getFaction()).contains(player.getCity().getFaction());
        boolean isEnemy = WarOfSquirrels.instance.getDiplomacyHandler()
                .getEnemies(chunk.getCity().getFaction()).contains(player.getCity().getFaction());
        boolean isFaction = chunk.getCity().getFaction().equals(player.getCity().getFaction());

        return chunk.getCity().getDefaultPermission().get(isAlly ?
                                        PermissionRelation.ALLY : (isEnemy ?
                                        PermissionRelation.ENEMY : isFaction ?
                                        PermissionRelation.FACTION : PermissionRelation.OUTSIDER));
    }

    private Permission checkTerritoryPermission(FullPlayer player, Territory territory) {
        boolean hasCity = player.getCity() != null;
        boolean hasFaction = hasCity && player.getCity().getFaction() != null;
        boolean territoryHasFaction = territory.getFaction() != null;

        if (!territoryHasFaction) return WarOfSquirrels.instance.getConfig().getDefaultNaturePermission();

        Permission customPerm = extractCustomPermission(player, territory.getFaction().getCustomPermissionList());

        if (customPerm != null) return customPerm;

        if (!hasFaction) return territory.getFaction().getDefaultPermission().get(PermissionRelation.OUTSIDER);

        boolean isAlly = WarOfSquirrels.instance.getDiplomacyHandler()
                .getAllies(territory.getFaction()).contains(player.getCity().getFaction());
        boolean isEnemy = WarOfSquirrels.instance.getDiplomacyHandler()
                .getEnemies(territory.getFaction()).contains(player.getCity().getFaction());
        boolean isFaction = territory.getFaction().equals(player.getCity().getFaction());

        return territory.getFaction().getDefaultPermission().get(isAlly ?
                PermissionRelation.ALLY : (isEnemy ?
                PermissionRelation.ENEMY : isFaction ?
                PermissionRelation.FACTION : PermissionRelation.OUTSIDER));
    }

    private Permission checkWarPermission(FullPlayer player, Chunk chunk) {
        boolean playerInWar = WarOfSquirrels.instance.getWarHandler().Contains(player);

        War warCity = WarOfSquirrels.instance.getWarHandler().getWar(chunk.getCity());
        War warPlayer = WarOfSquirrels.instance.getWarHandler().getWar(player);

        if (!playerInWar) return new Permission(false, false, false, false, false);
        if (!warCity.equals(warPlayer)) return null;

        boolean isAlly = WarOfSquirrels.instance.getDiplomacyHandler()
                .getAllies(chunk.getCity().getFaction()).contains(player.getCity().getFaction());
        boolean isEnemy = WarOfSquirrels.instance.getDiplomacyHandler()
                .getEnemies(chunk.getCity().getFaction()).contains(player.getCity().getFaction());
        boolean isFaction = chunk.getCity().getFaction().equals(player.getCity().getFaction());

        if (isAlly || isFaction)
            return new Permission(true, true, true, false, false);
        else if (isEnemy)
            return new Permission(true, false, false, false, false);
        else
            return new Permission(false, false, false, false, false);
    }

    private Permission getPermissionToCheck(Vector3 position, ResourceKey<Level> dimensionId, FullPlayer player) {
        Permission permission = null;

        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(position);
        boolean isThereCubo = cubo != null;

        Pair<Integer, Integer> chunkLocation = Utils.WorldToChunkCoordinates((int) position.x, (int) position.z);
        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(chunkLocation.getKey(), chunkLocation.getValue(), dimensionId);
        boolean isThereChunk = chunk != null;

        Pair<Integer, Integer> territoryLocation = Utils.ChunkToTerritoryCoordinates(chunkLocation.getKey(), chunkLocation.getValue());
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(territoryLocation.getKey(), territoryLocation.getValue());
        boolean isThereTerritory = territory != null; // ALWAYS TRUE IF OVERWORLD
        
        boolean isThereWarOnCity = isThereChunk && WarOfSquirrels.instance.getWarHandler().Contains(chunk.getCity());

        if (isThereWarOnCity)
            permission = checkWarPermission(player, chunk);

        if (permission != null) return permission;

        if (isThereCubo) return checkCuboPermission(player, cubo);
        if (isThereChunk) return checkChunkPermission(player, chunk);
        if (isThereTerritory) return checkTerritoryPermission(player, territory);

        return WarOfSquirrels.instance.getConfig().getDefaultNaturePermission();
    }
}
