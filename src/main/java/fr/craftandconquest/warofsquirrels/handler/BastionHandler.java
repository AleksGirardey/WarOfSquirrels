package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.upgrade.BastionUpgrade;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;

public class BastionHandler extends Handler<Bastion> {
    private final Map<City, List<Bastion>> bastionByCities;

    protected static String DirName = "/WorldData";
    protected static String JsonName = "/BastionHandler.json";

    public BastionHandler(Logger logger) {
        super("[WoS][BastionHandler]", logger);
        bastionByCities = new HashMap<>();

        if (!Init()) return;
        if (!Load(new TypeReference<>() {})) return;

        Log();
    }

    @Override
    protected boolean add(Bastion bastion) {
        if (!dataArray.contains(bastion))
            dataArray.add(bastion);

        City related = bastion.getRelatedCity();
        if (!bastionByCities.containsKey(related))
            bastionByCities.put(related, new ArrayList<>());
        bastionByCities.get(related).add(bastion);

        return true;
    }

    public boolean deleteCity(City city) {
        if (!bastionByCities.containsKey(city)) return true;

        List<Bastion> list = new ArrayList<>(bastionByCities.get(city));

        for (Bastion bastion : list) {
            if (!Delete(bastion)) return false;
        }
        return true;
    }

    @Override
    public boolean Delete(Bastion bastion) {
        if (!WarOfSquirrels.instance.getCuboHandler().deleteBastion(bastion)) return false;
        if (!WarOfSquirrels.instance.getChunkHandler().deleteBastion(bastion)) return false;
        if (!WarOfSquirrels.instance.getTerritoryHandler().delete(bastion)) return false;

        bastionByCities.get(bastion.getRelatedCity()).remove(bastion);
        dataArray.remove(bastion);

        return true;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Bastions generated : {1}",
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
    public void spreadPermissionDelete(IPermission target) { }

    public Bastion Create(Territory territory, City city, Vector2 chunkPosition, Vector3 playerPosition) {
        Bastion bastion = new Bastion();

        bastion.setBastionUuid(UUID.randomUUID());
        bastion.setName(territory.getName() + "'s bastion");
        bastion.SetCity(city);
        bastion.setProtected(true);
        bastion.setTerritoryPosition(new Vector2(territory.getPosX(), territory.getPosZ()));
        bastion.setBastionUpgrade(new BastionUpgrade());
        bastion.getBastionUpgrade().Init(bastion);

        add(bastion);

        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().CreateChunk((int) chunkPosition.x, (int) chunkPosition.y, bastion, Level.OVERWORLD);
        chunk.setHomeBlock(true);
        chunk.setRespawnPoint(playerPosition);

        territory.SetFortification(bastion);

        return bastion;
    }

    public Bastion get(UUID uuid) {
        for (Bastion bastion : dataArray) {
            if (bastion.getBastionUuid().equals(uuid))
                return bastion;
        }
        return null;
    }

    public Bastion get(String name) {
        for (Bastion bastion : dataArray) {
            if (bastion.getName().equals(name))
                return bastion;
        }
        return null;
    }

    public List<Bastion> get(City city) {
        return bastionByCities.getOrDefault(city, Collections.emptyList());
    }

    public void update() {
        for (Bastion bastion : dataArray)
            bastion.update();
    }

    public void updateDependencies() {
        for (Bastion bastion : dataArray) {
            bastion.updateDependencies();
            add(bastion);
        }
    }

    public void updateScore() {
        for (Bastion bastion : dataArray)
            bastion.updateScore();
    }
}
