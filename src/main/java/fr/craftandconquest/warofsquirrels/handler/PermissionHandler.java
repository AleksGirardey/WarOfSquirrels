package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.permission.CustomPermission;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

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

    private static final List<Item> authorizedItems = new ArrayList<>();
    private static final List<TagKey<Item>> authorizedItemsTag = new ArrayList<>();

    static {
        authorizedItems.add(Items.LADDER);
        authorizedItems.add(Items.GLASS);
        authorizedItems.add(Items.GLASS_PANE);
        authorizedItems.add(Items.SCAFFOLDING);

        authorizedItemsTag.add(ItemTags.DOORS);
        authorizedItemsTag.add(ItemTags.WOODEN_DOORS);
        authorizedItemsTag.add(ItemTags.TRAPDOORS);
        authorizedItemsTag.add(ItemTags.WOODEN_TRAPDOORS);
        authorizedItemsTag.add(ItemTags.FENCES);
        authorizedItemsTag.add(ItemTags.WOODEN_FENCES);
    }

    public boolean hasRightsTo(Rights rights, Object... objects) {
        ResourceKey<Level> dimension = Chunk.IdToDimension((String) objects[1]);
        return switch (rights) {
            case SET_HOMEBLOCK -> hasRightsToSetHomeBlock((FullPlayer) objects[0], (Chunk) objects[1]);
            case PLACE_IN_WAR -> hasRightsToPlaceInWar((Vector3) objects[0], dimension, (FullPlayer) objects[2], (BlockState) objects[3], (BlockPos) objects[4]);
            case DESTROY_IN_WAR -> hasRightsToDestroyInWar((Vector3) objects[0], dimension, (FullPlayer) objects[2], (BlockState) objects[3], (BlockPos) objects[4]);
            case BUILD -> hasRightsToBuild((Vector3) objects[0], dimension, (FullPlayer) objects[2]);
            case CONTAINER -> hasRightsToContainer((Vector3) objects[0], dimension, (FullPlayer) objects[2]);
            case SWITCH -> hasRightsToSwitch((Vector3) objects[0], dimension, (FullPlayer) objects[2]);
            case FARM -> hasRightsToFarm((Vector3) objects[0], dimension, (FullPlayer) objects[2]);
            case INTERACT -> hasRightsToInteract((Vector3) objects[0], dimension, (FullPlayer) objects[2]);
        };
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

    private boolean hasRightsToPlaceInWar(Vector3 position, ResourceKey<Level> dimensionId, FullPlayer player, BlockState block, BlockPos blockPos) {
            return getPermissionToCheck(position, dimensionId, player, block, blockPos).build;
    }

    private boolean hasRightsToDestroyInWar(Vector3 position, ResourceKey<Level> dimensionId, FullPlayer player, BlockState block, BlockPos blockPos) {
            return getPermissionToCheck(position, dimensionId, player, block, blockPos).build;
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
        boolean chunkHasFaction = chunk.getFortification().getFaction() != null;
        boolean sameCity = hasCity && chunk.getRelatedCity().equals(player.getCity());
        boolean isOwner = hasCity && player.getCity().getOwner().equals(player);
        boolean isAssistant = player.getAssistant();
        boolean isResident = player.getResident();

        Permission customPerm;

        customPerm = extractCustomPermission(player, chunk.getRelatedCity().getCustomPermissionList());

        if (customPerm != null) return customPerm;

        if (!hasCity) {
            return chunk.getRelatedCity().getDefaultPermission().get(PermissionRelation.OUTSIDER);
        }

        customPerm = extractCustomPermission(player.getCity(), chunk.getRelatedCity().getCustomPermissionList());
        if (customPerm != null) return customPerm;

        if (player.getCity().getFaction() != null) {
            customPerm = extractCustomPermission(player.getCity().getFaction(), chunk.getRelatedCity().getCustomPermissionList());
            if (customPerm != null) return customPerm;
        }

        if (sameCity) {
            if (isOwner || isAssistant) return new Permission(true, true, true, true, true);

            return isResident ? chunk.getRelatedCity().getDefaultPermission().get(PermissionRelation.RESIDENT) :
                    chunk.getRelatedCity().getDefaultPermission().get(PermissionRelation.RECRUIT);
        }

        if (!chunkHasFaction || !hasFaction) return chunk.getRelatedCity().getDefaultPermission().get(PermissionRelation.OUTSIDER);

        boolean isAlly = WarOfSquirrels.instance.getDiplomacyHandler()
                .getAllies(chunk.getRelatedCity().getFaction()).contains(player.getCity().getFaction());
        boolean isEnemy = WarOfSquirrels.instance.getDiplomacyHandler()
                .getEnemies(chunk.getRelatedCity().getFaction()).contains(player.getCity().getFaction());
        boolean isFaction = chunk.getRelatedCity().getFaction().equals(player.getCity().getFaction());

        return chunk.getRelatedCity().getDefaultPermission().get(isAlly ?
                                        PermissionRelation.ALLY : (isEnemy ?
                                        PermissionRelation.ENEMY : isFaction ?
                                        PermissionRelation.FACTION : PermissionRelation.OUTSIDER));
    }

    private Permission checkTerritoryPermission(FullPlayer player, Territory territory) {
        boolean hasCity = player.getCity() != null;
        boolean hasFaction = hasCity && player.getCity().getFaction() != null;
        boolean territoryHasFaction = territory.getFaction() != null;
        boolean sameFaction = hasFaction && territoryHasFaction && player.getCity().getFaction().equals(territory.getFaction());
        boolean isOwner = hasCity && player.getCity().getOwner().equals(player);
        boolean isAssistant = player.getAssistant();
        boolean isResident = player.getResident();

        if (!territoryHasFaction) return WarOfSquirrels.instance.getConfig().getDefaultNaturePermission();

        Permission customPerm = extractCustomPermission(player, territory.getFaction().getCustomPermissionList());

        if (customPerm != null) return customPerm;

        if (!hasFaction) return territory.getFaction().getDefaultPermission().get(PermissionRelation.OUTSIDER);

        if (sameFaction) {
            if (isOwner || isAssistant) return new Permission(true, true, true, true, true);

            return isResident ? territory.getFaction().getDefaultPermission().get(PermissionRelation.RESIDENT) :
                    territory.getFaction().getDefaultPermission().get(PermissionRelation.RECRUIT);
        }

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

    private Permission checkWarPermission(War targetWar, War playerWar, Faction targetFaction, Faction playerFaction, FullPlayer player, BlockState block, BlockPos blockPos) {

        if (block == null) return null;

        if (!player.isInWar() || playerFaction == null)
            return new Permission(false, false, false, false, false);

        if (!targetWar.equals(playerWar))
            return null;

        if (targetWar.getCityDefender().equals(player.getCity())) {

            Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(blockPos.getX(), blockPos.getZ(), player.getLastDimensionKey());

            if (isAuthorized(block.getBlock()) && chunk != null && !chunk.equals(targetWar.getCityDefender().getHomeBlock()))
                return new Permission(true, true, true, true, true);
            else
                return new Permission(false, true, true, false, false);
        }

        boolean isAlly = WarOfSquirrels.instance.getDiplomacyHandler()
                .getAllies(targetFaction).contains(playerFaction);
        boolean isEnemy = WarOfSquirrels.instance.getDiplomacyHandler().getEnemies(targetFaction).contains(playerFaction)
                || WarOfSquirrels.instance.getDiplomacyHandler().getEnemies(playerFaction).contains(targetFaction);
        boolean isFaction = targetFaction.equals(playerFaction);

        if (isAlly || isFaction) {
            if (isAuthorized(block.getBlock()))
                return new Permission(true, true, true, true, true);
        } else if (isEnemy) {
            if (isAuthorized(block.getBlock()) || targetWar.isCaptured(new Vector3(blockPos.getX(), blockPos.getY(), blockPos.getZ())))
                return new Permission(true, false, false, false, false);
        }

        return new Permission(false, false, false, false, false);
    }

    private Permission checkWarPermission(FullPlayer player, Territory territory, BlockState block, BlockPos blockPos) {
        War targetWar = WarOfSquirrels.instance.getWarHandler().getWar(territory);
        War playerWar = WarOfSquirrels.instance.getWarHandler().getWar(player);
        Faction targetFaction = territory.getFaction();
        Faction playerFaction = player.getCity() != null ? player.getCity().getFaction() : null;

        return checkWarPermission(targetWar, playerWar, targetFaction, playerFaction, player, block, blockPos);
    }

    private Permission checkWarPermission(FullPlayer player, Chunk chunk, BlockState block, BlockPos blockPos) {
        War targetWar = WarOfSquirrels.instance.getWarHandler().getWar(chunk.getRelatedCity());
        War playerWar = WarOfSquirrels.instance.getWarHandler().getWar(player);
        Faction targetFaction = chunk.getRelatedCity().getFaction();
        Faction playerFaction = player.getCity() != null ? player.getCity().getFaction() : null;

        return checkWarPermission(targetWar, playerWar, targetFaction, playerFaction, player, block, blockPos);
    }

    private Permission getPermissionToCheck(Vector3 position, ResourceKey<Level> dimensionId, FullPlayer player) {
        return getPermissionToCheck(position, dimensionId, player, null, null);
    }

    private Permission getPermissionToCheck(Vector3 position, ResourceKey<Level> dimensionId, FullPlayer player, BlockState block, BlockPos blockPos) {
        Permission permission = null;

        AdminCubo adminCubo = WarOfSquirrels.instance.getAdminHandler().get(position, dimensionId);
        boolean isThereAdminCubo = adminCubo != null;

        if (isThereAdminCubo) return adminCubo.getPermission();

        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(position);
        boolean isThereCubo = cubo != null;

        Vector2 chunkLocation = Utils.FromWorldToChunk((int) position.x, (int) position.z);
        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk((int) chunkLocation.x, (int) chunkLocation.y, dimensionId);
        boolean isThereChunk = chunk != null;

        Vector2 territoryLocation = Utils.FromChunkToTerritory((int) chunkLocation.x, (int) chunkLocation.y);
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get((int) territoryLocation.x, (int) territoryLocation.y);
        boolean isThereTerritory = territory != null; // ALWAYS TRUE IF OVERWORLD
        boolean isThereFortification = isThereTerritory && territory.getFortification() != null; // ALWAYS TRUE IF OVERWORLD

        boolean isThereWarOnCity = isThereChunk && WarOfSquirrels.instance.getWarHandler().Contains(chunk.getRelatedCity());
        boolean isThereWarOnTerritory = isThereFortification && WarOfSquirrels.instance.getWarHandler().Contains(territory);

        if (isThereWarOnCity)
            permission = checkWarPermission(player, chunk, block, blockPos);
        else if (isThereWarOnTerritory)
            permission = checkWarPermission(player, territory, block, blockPos);

        if (permission != null) return permission;

        if (isThereCubo) return checkCuboPermission(player, cubo);
        if (isThereChunk) return checkChunkPermission(player, chunk);
        //if (isThereTerritory) return checkTerritoryPermission(player, territory);

        return WarOfSquirrels.instance.getConfig().getDefaultNaturePermission();
    }

    private boolean isAuthorized(Block block) {
        ItemStack stack = new ItemStack(block);

        for (Item item : authorizedItems) {
            if (stack.is(item)) return true;
        }

        for (TagKey<Item> tag : authorizedItemsTag) {
            if (stack.is(tag)) return true;
        }

        return false;
    }
}
