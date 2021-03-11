package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;

public class CityHandler extends Handler<City> {
    private final Map<UUID, City> cityMap;

    protected static String DirName = "/WorldData";
    protected static String JsonName = "/CityHandler.json";

    public CityHandler(Logger logger) {
        super("[WoS][CityHandler]", logger);
        cityMap = new HashMap<>();

        if (!Init()) return;
        if (!Load(new TypeReference<List<City>>() {})) return;

        Log();
    }

    @Override
    public boolean add(City city) {
        if (cityMap.containsKey(city.getCityUuid())) return false;

        if (!dataArray.contains(city)) {
            if (dataArray.size() == 0)
                dataArray = new ArrayList<>();
            dataArray.add(city);
        }

        cityMap.put(city.getCityUuid(), city);
        return true;
    }

    public City CreateCity(String name, String tag, Player owner) {
        City city = new City();

        city.setCityUuid(UUID.randomUUID());
        city.displayName = name;
        city.tag = tag;
        city.SetOwner(owner);
        city.SetRank(0);
        city.setCustomPermission(new HashMap<>());
        city.setDefaultPermission(new HashMap<>(WarOfSquirrels.instance.config.getConfiguration().getPermissionMap()));

        for (Permission permission : city.getDefaultPermission().values()) {
            permission.setUuid(UUID.randomUUID());
        }

        if (!add(city))
            return null;

        Save(cityMap.values());
        LogCityCreation(city);
        return city;
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

    @Override
    public void spreadPermissionDelete(IPermission target) {
        for (City city : cityMap.values()) {
            city.getCustomPermission().remove(target);
        }
    }

    public City getCity(String cityName) {
        for (City city : dataArray) {
            if (city.displayName.equals(cityName))
                return city;
        }
        return null;
    }

    public City getCity(UUID uuid) { return cityMap.get(uuid); }

//    public List<City> getAll() { return dataArray; }

    @Override
    public boolean Delete(City city) {
        WarOfSquirrels.instance.spreadPermissionDelete(city);

        if (!WarOfSquirrels.instance.getChunkHandler().deleteCity(city)) return false;

        for (Player player : city.getCitizens()) {
            player.setAssistant(false);
            player.setCity(null);
        }

        cityMap.remove(city.getCityUuid());
        city.getOwner().setCity(null);

        Save(cityMap.values());

        return true;
    }

    public void NewCitizen(Player player, City city) {
        player.setCity(city);
        assert city.addCitizen(player);
        WarOfSquirrels.instance.getPlayerHandler().Save();
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(city, null, new StringTextComponent(player.getDisplayName() + " has join the city."), true);
    }

    public void RemoveCitizen(Player player) {
        player.getCity().removeCitizen(player, false);
        player.setCity(null);
    }

    private void LogCityCreation(City city) {
        Logger.info(PrefixLogger + city + " created");
    }

    public List<City> getCities(Faction faction) {
        List<City> cities = new ArrayList<>();

        for (City city : dataArray) {
            if (city.getFaction() == faction)
                cities.add(city);
        }

        return cities;
    }

    public List<Player> getAssistants(City city) {
        List<Player> res = new ArrayList<>();

        for (Player player : city.getCitizens()) {
            if (player.getAssistant())
                res.add(player);
        }

        return res;
    }

    public List<String> getAssistants(UUID uuid) {
        List<Player> assistants = getAssistants(cityMap.get(uuid));
        List<String> res = new ArrayList<>();

        for (Player player : assistants) {
            res.add(player.getDisplayName());
        }

        return res;
    }

    public void SetCustomPermission(IPermission target, Permission permission, City city) {
        if (city.getCustomPermission().containsKey(target))
            city.getCustomPermission().replace(target, permission);
        else
            city.getCustomPermission().put(target, permission);
        Save(dataArray);
    }

    public void SetDefaultPermission(PermissionRelation relation, Permission permission, City city) {
        city.getDefaultPermission().replace(relation, permission);
        Save(dataArray);
    }

    public Collection<String> getAllAsCollection() {
        List<String> cities = new ArrayList<>();

        for (City city : dataArray)
            cities.add(city.getDisplayName());

        return cities;
    }
}
