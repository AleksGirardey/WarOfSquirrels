package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.object.cuboide.VectorCubo;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class AdminHandler extends Handler<AdminCubo> {

    private Map<String, AdminCubo> adminCuboMap;

    public AdminHandler(Logger logger) {
        super("[WoS][AdminHandler]", logger);
    }

    @Override
    protected void InitVariables() {
        adminCuboMap = new HashMap<>();
    }

    public AdminCubo CreateTeleporter(FullPlayer player, String name) {
        AdminCubo cubo = CreateCubo(player, name);
        Player entity = player.getPlayerEntity();

        cubo.setTeleport(true);
        cubo.setRespawnPoint(new Vector3(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ()));
        cubo.setRespawnDimension(player.getPlayerEntity().level.dimension().location().getPath());

        if (add(cubo)) {
            player.sendMessage(ChatText.Success("Teleportation cubo created."));
            return cubo;
        }
        return null;
    }

    public AdminCubo CreateProtection(FullPlayer player, String name) {
        AdminCubo cubo = CreateCubo(player, name);

        cubo.setTeleport(false);
        cubo.setRespawnDimension(player.getPlayerEntity().level.dimension().location().getPath());

        if (add(cubo)) {
            player.sendMessage(ChatText.Success("Protection cubo created."));
            return cubo;
        }
        return null;
    }

    private AdminCubo CreateCubo(FullPlayer player, String name) {
        Pair<Vector3, Vector3> points = WarOfSquirrels.instance.getCuboHandler().getPoints(player);
        VectorCubo vector = new VectorCubo(points.getKey(), points.getValue());
        AdminCubo cubo = new AdminCubo();

        cubo.setUuid(UUID.randomUUID());
        cubo.setDisplayName(name);
        cubo.setVector(vector);
        cubo.setPermission(new Permission(false, false, true, false, false));
        cubo.setClearInventoryOnTp(false);

        return cubo;
    }

    public List<AdminCubo> getAllTp() {
        return super.getAll().stream().filter(AdminCubo::isTeleport).toList();
    }

    public AdminCubo get(String name) {
        return adminCuboMap.get(name);
    }

    public AdminCubo get(UUID id) {
        return dataArray.stream()
                .filter(adminCubo -> adminCubo.getUuid().equals(id))
                .findFirst().orElse(null);
    }

    public AdminCubo get(Vector3 block, ResourceKey<Level> dim) {
        for (AdminCubo cubo : dataArray) {
            if (cubo.getVector().contains(block) && cubo.getDimensionKey() == dim)
                return cubo;
        }
        return null;
    }

    public List<AdminCubo> getAll(Vector3 block, ResourceKey<Level> dim) {
        List<AdminCubo> list = new ArrayList<>();

        for (AdminCubo cubo : dataArray) {
            if (cubo.getVector().contains(block) && cubo.getDimensionKey() == dim)
                list.add(cubo);
        }

        return list;
    }

    @Override
    protected boolean add(AdminCubo value) {
        super.add(value);

        if (!adminCuboMap.containsKey(value.getDisplayName()))
            adminCuboMap.put(value.getDisplayName(), value);

        return true;
    }

    @Override
    protected void CustomLoad(File configFile) throws IOException {
        dataArray = jsonArrayToList(configFile, AdminCubo.class);
    }

    @Override
    public boolean Delete(AdminCubo value) {
        adminCuboMap.remove(value.getDisplayName());
        dataArray.remove(value);
        return true;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Admin cubo generated : {1}",
                PrefixLogger, dataArray.size()));
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {
        // Nothing to do
    }
}
