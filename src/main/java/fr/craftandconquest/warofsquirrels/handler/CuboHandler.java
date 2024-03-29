package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.cuboide.VectorCubo;
import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.faction.guild.Guild;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CuboHandler extends Handler<Cubo> {
    private Map<FullPlayer, Pair<Vector3, Vector3>> points = new HashMap<>();
    private Map<String, Cubo> cuboMapFromName = new HashMap<>();

    public CuboHandler(Logger logger) {
        super("[WoS][CuboHandler]", logger);
    }

    @Override
    protected void InitVariables() {
        points = new HashMap<>();
        cuboMapFromName = new HashMap<>();
    }

    public Cubo CreateCubo(FullPlayer player, String name) {
        if (!points.containsKey(player)) {
            player.sendMessage(ChatText.Error("You need to activate the cubo mode first"));
            return null;
        }

        Pair<Vector3, Vector3> cuboPoints = points.get(player);
        VectorCubo vectorCubo = new VectorCubo(cuboPoints.getKey(), cuboPoints.getValue());
        Cubo cubo, parent = getParent(vectorCubo);

        City city = player.getCity();

        cubo = new Cubo();
        cubo.setUuid(UUID.randomUUID());
        cubo.setCity(city);
        cubo.setDisplayName(name);
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
            player.sendMessage(ChatText.Colored("Cubo created : " + cubo, ChatFormatting.GOLD));
            Logger.info(MessageFormat.format("{0}[New] {1} created a cubo : {2}",
                    PrefixLogger, player.getDisplayName(), cubo));
            return cubo;
        }
        return null;
    }

    @Override
    protected boolean add(Cubo value) {
        super.add(value);

        if (!cuboMapFromName.containsKey(value.getDisplayName()))
            cuboMapFromName.put(value.getDisplayName(), value);

        return true;
    }

    @Override
    protected void CustomLoad(File configFile) throws IOException {
        dataArray = jsonArrayToList(configFile, Cubo.class);
    }

    public boolean Delete(String name) {
        Cubo cubo = getCubo(name);

        if (cubo == null) return false;

        return Delete(cubo);
    }

    @Override
    public boolean Delete(Cubo value) {
        super.Delete(value);

        cuboMapFromName.remove(value.getDisplayName());
        return true;
    }

    public Cubo getCubo(String name) {
        return cuboMapFromName.get(name);
    }

    public List<Cubo> getCubo(City city) {
        List<Cubo> result = new ArrayList<>();

        for (Cubo c : dataArray) {
            if (c.getOwner().getCity() == city)
                result.add(c);
        }

        return result;
    }

    public List<Cubo> getCubo(FullPlayer player) {
        return dataArray.stream().filter(cubo -> {
            return cubo.getOwner().equals(player) /*|| cubo.getLoan().getLoaner().equals(player)*/;
        }).collect(Collectors.toList());
    }

    public Cubo getCubo(Vector3 block) {
        Cubo last = null;

        for (Cubo c : dataArray) {
            if (c.getVector().contains(block)) {
                if (last == null || last.getPriority() < c.getPriority()) {
                    last = c;
                }
            }
        }
        return last;
    }

    public Cubo getParent(VectorCubo vector) {
        Cubo last = null;

        for (Cubo c : dataArray) {
            if (c.getVector().contains(vector.getA()) &&
                    c.getVector().contains(vector.getB())) {
                if (last == null || last.getPriority() < c.getPriority())
                    last = c;
            }
        }

        return last;
    }

    public void activateCuboMode(FullPlayer player) {
        points.computeIfAbsent(player, CuboHandler::newCubo);
    }

    public Pair<Vector3, Vector3> getPoints(FullPlayer player) {
        return points.get(player);
    }

    private static Pair<Vector3, Vector3> newCubo(FullPlayer player) {
        player.sendMessage(ChatText.Success(
                "-=== CuboVector mode [ON] ===-"));
        return (Pair.of(null, null));
    }

    public void deactivateCuboMode(FullPlayer player) {
        if (points.get(player) != null) {
            points.remove(player);
            player.sendMessage(ChatText.Success(
                    "-=== CuboVector mode [OFF] ===-"));
        }
    }

    public boolean playerExists(FullPlayer player) {
        return points.containsKey(player);
    }

    public void set(FullPlayer player, Vector3 block, boolean aOrB) {
        MutableComponent message = ChatText.Colored("", ChatFormatting.LIGHT_PURPLE);

        Vector3 left;
        Vector3 right;

        if (aOrB) {
            left = block;
            right = points.get(player).getRight();
            message.append("Block A");
        } else {
            left = points.get(player).getLeft();
            right = block;
            message.append("Block B");
        }

        points.replace(player, Pair.of(left, right));

        message.append(" set at the position [" + block.x + ";" + block.y + ";" + block.z + "]");
        player.sendMessage(message);
    }

    public boolean deleteCity(City city) {
        List<UUID> removeList = new ArrayList<>();

        dataArray.forEach(c -> {
            if (c.getCity() != null && c.getCity().equals(city))
                removeList.add(c.getUuid());
        });

        removeList.forEach(uuid -> Delete(get(uuid)));
        Save();
        return true;
    }

    public boolean deleteGuild(Guild guild) {
        List<UUID> removeList = new ArrayList<>();

        dataArray.forEach(cubo -> {
            if (cubo.getGuild() != null && cubo.getGuild().equals(guild))
                removeList.add(cubo.getUuid());
        });

        removeList.forEach(uuid -> Delete(get(uuid)));
        Save();
        return true;
    }

    public boolean deleteBastion(Bastion bastion) {
        List<UUID> removeList = new ArrayList<>();
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(bastion);

        dataArray.forEach(cubo -> {
            Vector2 pos = Utils.FromWorldToTerritory((int) cubo.getVector().getA().x, (int) cubo.getVector().getA().z);
            if (territory.getPosX() == pos.x && territory.getPosZ() == pos.y)
                removeList.add(cubo.getUuid());
        });

        removeList.forEach(uuid -> Delete(get(uuid)));
        Save();
        return true;
    }

    public List<String> getStringFromPlayer(FullPlayer player) {
        List<String> names = new ArrayList<>();
        List<Cubo> cubos = getCubo(player);

        for (Cubo c : cubos) {
            names.add(c.getDisplayName());
        }

        return names;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Cubo generated : {1}", PrefixLogger, dataArray.size()));
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {
        for (Cubo cubo : dataArray) {
            cubo.SpreadPermissionDelete(target);
        }
    }

    @Override
    protected String getDirName() {
        return super.getDirName() + "/Faction";
    }
}
