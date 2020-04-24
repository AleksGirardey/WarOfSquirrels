package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.city.City;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;

public class CityHandler extends Handler<City> {
    private final Map<Integer, City> cityMap;

    protected String DirName = "/WorldData";
    protected String JsonName = "/CityHandler.json";

    public CityHandler(Logger logger) {
        super("[WoS][CityHandler]", logger);
        cityMap = new HashMap<>();

        if (!Init()) return;
        if (!Load(new TypeReference<List<City>>() {})) return;

        Log();
    }

    @Override
    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(this::add);
        return false;
    }

    public boolean add(City city) {
        int cityId = getCityId(city);
        city.setCityId(cityId);
        if (cityMap.containsKey(cityId)) return false;

        if (!dataArray.contains(city))
            dataArray.add(city);

        cityMap.put(cityId, city);
        Save(cityMap.values());
        LogCityCreation(city);
        return true;
    }

    public boolean CreateCity(String name, String tag, Player owner) {
        City city = new City();

        city.displayName = name;
        city.tag = tag;
        city.SetOwner(owner);
        city.SetRank(0);

        return add(city);
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Cities generated : {1}",
                PrefixLogger, dataArray.size()));
    }

    @Override
    public String getConfigDir() {
        return WarOfSquirrels.warOfSquirrelsConfigDir + DirName;
    }

    @Override
    protected String getConfigPath() {
        return WarOfSquirrels.warOfSquirrelsConfigDir + DirName + JsonName;
    }

    public int getCityId(City city) {
        String seedString = city.displayName + city.tag;
        int seed = 0;
        for (char c : seedString.toCharArray()) {
            seed += c - 'a' + 1;
        }
        return seed;
    }

    @Override
    public boolean Delete(City city) {
        /*
         * ToDo : Delete Permissions
         */

        if (!WarOfSquirrels.instance.getChunkHandler().deleteCity(city)) return false;

        for (Player player : city.getCitizens()) {
            player.setAssistant(false);
            player.setCity(null);
        }

        cityMap.remove(city.getCityId());
        city.getOwner().setCity(null);

        return true;
    }

    private void LogCityCreation(City city) {
        Logger.info(PrefixLogger + city + " created");
    }

    public List<Player> getAssistants(City city) {
        List<Player> res = new ArrayList<>();

        for (Player player : city.getCitizens()) {
            if (player.getAssistant())
                res.add(player);
        }

        return res;
    }

    public List<String> getAssistants(int id) {
        List<Player> assistants = getAssistants(cityMap.get(id));
        List<String> res = new ArrayList<>();

        for (Player player : assistants) {
            res.add(player.getDisplayName());
        }

        return res;
    }
}
