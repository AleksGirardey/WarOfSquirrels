package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.cuboide.VectorCubo;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CuboHandler extends Handler<Cubo> {
    private final Map<Player, Pair<Vector3, Vector3>>   points = new HashMap<>();
    private final Map<UUID, Cubo>                       cuboMap = new HashMap<>();
    private final Map<String, Cubo>                     cuboMapFromName = new HashMap<>();

    protected static String DirName = "/WorldData";
    protected static String JsonName = "CuboHandler.json";

    public CuboHandler(Logger logger) {
        super("[WoS][CuboHandler]", logger);

        if (!Init()) return;
        if (!Load(new TypeReference<List<Cubo>>() {})) return;

        Log();
    }

    public Cubo CreateCubo(Player player, String name) {
        if (!points.containsKey(player)) {
            player.getPlayerEntity().sendMessage(new StringTextComponent("You need to activate the cubo mode first")
                    .applyTextStyle(TextFormatting.RED));
            return null;
        }

        Pair<Vector3, Vector3> cuboPoints = points.get(player);
        VectorCubo vectorCubo = new VectorCubo(cuboPoints.getKey(), cuboPoints.getValue());
        Cubo cubo, parent = getParent(vectorCubo);

        cubo = new Cubo();
        cubo.setName(name);
        cubo.setOwner(player);
        cubo.setPermissionIn(new Permission(true, true, true, true, true));
        cubo.setPermissionOut(new Permission(false, false, false, false, false));
        if (parent != null) {
            cubo.setParent(parent);
            cubo.setPriority(parent.getPriority() + 1);
        } else {
            cubo.setPriority(0);
        }
        cubo.setVector(vectorCubo);

        if (add(cubo)) {
            player.getPlayerEntity().sendMessage(new StringTextComponent("Cubo created : " + cubo)
                    .applyTextStyle(TextFormatting.GOLD));
            Logger.info(MessageFormat.format("{0}[New] %s created a cubo : %s",
                    PrefixLogger, player.getDisplayName(), cubo));
            return cubo;
        }
        return null;
    }

    @Override
    protected boolean add(Cubo value) {
        if (dataArray.contains(value)) return false;

        dataArray.add(value);
        cuboMap.put(value.getUuid(), value);
        cuboMapFromName.put(value.getName(), value);

        return true;
    }

    public boolean Delete(String name) {
        Cubo cubo = getCubo(name);

        if (cubo == null) return false;
        return Delete(cubo);
    }

    @Override
    public boolean Delete(Cubo value) {
        if (!dataArray.contains(value)) return false;

        dataArray.remove(value);
        cuboMap.remove(value.getUuid());
        cuboMapFromName.remove(value.getName());
        return false;
    }

    public void updateDependencies() {
        for (Cubo cubo : dataArray)
            cubo.UpdateDependencies();
    }

    public Cubo getCubo(String name) {
        return cuboMapFromName.get(name);
    }

    public Cubo getCubo(UUID uuid) {
        return cuboMap.get(uuid);
    }

    public List<Cubo> getCubo(City city) {
        List<Cubo>              result = new ArrayList<Cubo>();

        for (Cubo c : dataArray) {
            if (c.getOwner().getCity() == city)
                result.add(c);
        }

        return result;
    }

    public List<Cubo> getCubo(Player player) {
        return dataArray.stream().filter(cubo -> {
            return cubo.getOwner().equals(player) /*|| cubo.getLoan().getLoaner().equals(player)*/;
        }).collect(Collectors.toList());
    }

    public Cubo getCubo(Vector3 block) {
        Cubo            last = null;

        for (Cubo c : dataArray) {
            if (c.getVector().contains(block)) {
                if (last == null || last.getPriority() < c.getPriority()) {
                    last = c;
                }
            }
        }
        return  last;
    }

    public Cubo getParent(VectorCubo vector) {
        Cubo            last = null;

        for (Cubo c : dataArray) {
            if (c.getVector().contains(vector.getA()) &&
                    c.getVector().contains(vector.getB())) {
                if (last == null || last.getPriority() < c.getPriority())
                    last = c;
            }
        }

        return last;
    }

    public void activateCuboMode(Player player) {
        points.computeIfAbsent(player, CuboHandler::newCubo);
    }

    public Pair<Vector3, Vector3> getPoints(Player player) {
        return points.get(player);
    }

    private static Pair<Vector3, Vector3> newCubo(Player player) {
        player.getPlayerEntity().sendMessage(new StringTextComponent(
                "-=== CuboVector mode [ON] ===-"));
        return (Pair.of(null, null));
    }

    public void deactivateCuboMode(Player player) {
        if (points.get(player) != null) {
            points.remove(player);
            player.getPlayerEntity().sendMessage(new StringTextComponent(
                    "-=== CuboVector mode [OFF] ===-"));
        }
    }

    public boolean playerExists(Player player) {
        return points.containsKey(player);
    }

    public void set(Player player, Vector3 block, boolean aOrB) {
        StringTextComponent message = new StringTextComponent("");

        message.applyTextStyle(TextFormatting.LIGHT_PURPLE);

        Vector3 left;
        Vector3 right;

        if (aOrB) {
            left = block;
            right = points.get(player).getRight();
            message.appendText("Block A");
        } else {
            left = points.get(player).getLeft();
            right = block;
            message.appendText("Block B");
        }

        points.replace(player, Pair.of(left, right));

        message.appendText(" set at the position [" + block.x + ";" + block.y + ";" + block.z + "]");
        player.getPlayerEntity().sendMessage(message);
    }

    public void deleteCity(City city) {
        List<UUID> removeList = new ArrayList<>();

        dataArray.forEach(c -> {
            if (c.getCity().equals(city))
                removeList.add(c.getUuid());
        });

        removeList.forEach(uuid -> Delete(getCubo(uuid)));
        Save();
    }

    public List<String>     getStringFromPlayer(Player player) {
        List<String>        names = new ArrayList<>();
        List<Cubo>          cubos = getCubo(player);

        for (Cubo c : cubos) {
            names.add(c.getName());
        }

        return names;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Cubo generated : {1}",
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

    public void UpdateDependencies() {
        dataArray.forEach(Cubo::UpdateDependencies);
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {
        for (Cubo cubo : dataArray) {
            cubo.SpreadPermissionDelete(target);
        }
    }
}
