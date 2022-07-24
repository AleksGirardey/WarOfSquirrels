package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.upgrade.bastion.BastionUpgrade;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class BastionHandler extends UpdatableHandler<Bastion> {
    private Map<City, List<Bastion>> bastionByCities;

    public BastionHandler(Logger logger) {
        super("[WoS][BastionHandler]", logger);
    }

    @Override
    protected void InitVariables() {
        bastionByCities = new HashMap<>();
    }

    @Override
    protected boolean add(Bastion bastion) {
        super.add(bastion);

        City related = bastion.getRelatedCity();
        if (!bastionByCities.containsKey(related))
            bastionByCities.put(related, new ArrayList<>());
        bastionByCities.get(related).add(bastion);

        return true;
    }

    @Override
    protected void CustomLoad(File configFile) throws IOException {
        dataArray = jsonArrayToList(configFile, Bastion.class);
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
        if (!WarOfSquirrels.instance.getChunkHandler().delete(bastion)) return false;
        if (!WarOfSquirrels.instance.getInfluenceHandler().delete(bastion)) return false;
        if (!WarOfSquirrels.instance.getTerritoryHandler().delete(bastion)) return false;

        bastionByCities.get(bastion.getRelatedCity()).remove(bastion);
        dataArray.remove(bastion);

        return true;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Bastions generated : {1}", PrefixLogger, dataArray.size()));
    }
    @Override
    public void spreadPermissionDelete(IPermission target) { }

    public Bastion Create(Territory territory, City city, Vector2 chunkPosition, Vector3 playerPosition) {
        Bastion bastion = new Bastion();

        bastion.setUuid(UUID.randomUUID());
        bastion.setDisplayName(territory.getDisplayName() + "'s bastion");
        bastion.setCity(city);
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

    public List<Bastion> get(City city) {
        return bastionByCities.getOrDefault(city, Collections.emptyList());
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

    @Override
    protected String getDirName() {
        return super.getDirName() + "/Faction/City";
    }
}
