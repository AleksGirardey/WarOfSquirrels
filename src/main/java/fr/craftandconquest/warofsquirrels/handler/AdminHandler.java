package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.object.cuboide.VectorCubo;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminHandler extends Handler<AdminCubo> {
    protected static String DirName = "/WorldData";
    protected static String JsonName = "/AdminHandler.json";

    private final Map<String, AdminCubo> adminCuboMap = new HashMap<>();

    public AdminHandler(Logger logger) {
        super("[WoS][AdminHandler]", logger);

        if (!Init()) return;
        if (!Load(new TypeReference<>() {})) return;

        Log();
    }

    public AdminCubo CreateTeleporter(FullPlayer player, String name) {
        AdminCubo cubo = CreateCubo(player, name);
        Player entity = player.getPlayerEntity();

        cubo.setTeleporter(true);
        cubo.setRespawnPoint(new Vector3(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ()));
        cubo.setRespawnDimension(player.getLastDimensionKey().location().getPath());

        if (add(cubo)) {
            player.sendMessage(ChatText.Success("Teleportation cubo created."));
            return cubo;
        }
        return null;
    }

    public AdminCubo CreateProtection(FullPlayer player, String name) {
        AdminCubo cubo = CreateCubo(player, name);

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
        cubo.setName(name);
        cubo.setVector(vector);

        return cubo;
    }

    public List<AdminCubo> getAllTp() {
        return super.getAll().stream().filter(AdminCubo::isTeleporter).toList();
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

    @Override
    protected boolean add(AdminCubo value) {
        if (!dataArray.contains(value))
            dataArray.add(value);

        if (!adminCuboMap.containsKey(value.getName()))
            adminCuboMap.put(value.getName(), value);

        return true;
    }

    @Override
    public boolean Delete(AdminCubo value) {
        adminCuboMap.remove(value.getName());
        dataArray.remove(value);
        return true;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Admin cubo generated : {1}",
                PrefixLogger, dataArray.size()));
    }

    @Override
    public String getConfigDir() {
        return WarOfSquirrels.warOfSquirrelsConfigDir + DirName;
    }

    @Override
    protected String getConfigPath() {
        return getConfigDir() + JsonName;
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {
        // Nothing to do
    }
}