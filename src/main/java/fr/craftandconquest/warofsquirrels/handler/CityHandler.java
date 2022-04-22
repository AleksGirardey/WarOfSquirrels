package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.CustomPermission;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import fr.craftandconquest.warofsquirrels.object.upgrade.CityUpgrade;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
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
        if (!Load(new TypeReference<>() {})) return;

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

    public City CreateCity(String name, String tag, FullPlayer owner, Territory territory) {
        City city = new City();

        city.setCityUuid(UUID.randomUUID());
        city.displayName = name;
        city.tag = tag;
        city.setTerritory(territory);
        city.SetOwner(owner);
        city.setCityUpgrade(new CityUpgrade());
        city.getCityUpgrade().Init(city);
        city.setCustomPermission(new HashMap<>());
        city.setDefaultPermission(new HashMap<>(WarOfSquirrels.instance.config.getConfiguration().getPermissionMap()));

        for (Permission permission : city.getDefaultPermission().values()) {
            permission.setUuid(UUID.randomUUID());
        }

        if (!add(city))
            return null;

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

    public City getCity(UUID uuid) {
        return cityMap.get(uuid);
    }

//    public List<City> getAll() { return dataArray; }

    @Override
    public boolean Delete(City city) {
        WarOfSquirrels.instance.spreadPermissionDelete(city);

        if (!WarOfSquirrels.instance.getInfluenceHandler().delete(city)) return false;
        if (!WarOfSquirrels.instance.getTerritoryHandler().delete(city)) return false;
        if (!WarOfSquirrels.instance.getBastionHandler().deleteCity(city)) return false;
        if (!WarOfSquirrels.instance.getCuboHandler().deleteCity(city)) return false;
        if (!WarOfSquirrels.instance.getChunkHandler().deleteCity(city)) return false;

        for (FullPlayer player : city.getCitizens()) {
            player.setAssistant(false);
            player.setResident(false);
            player.setCity(null);
        }

        cityMap.remove(city.getCityUuid());
        city.getOwner().setCity(null);
        dataArray.remove(city);

        return true;
    }

    public void NewCitizen(FullPlayer player, City city) {
        player.setCity(city);
        assert city.addCitizen(player);
        WarOfSquirrels.instance.getPlayerHandler().Save();
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(city, null, ChatText.Success(player.getDisplayName() + " has join the city."), true);
    }

    public void RemoveCitizen(FullPlayer player) {
        player.getCity().removeCitizen(player, false);
        player.setCity(null);
    }

    private void LogCityCreation(City city) {
        Logger.info(PrefixLogger + city + " created");
    }

    public List<City> getCities(Faction faction) {
        List<City> cities = new ArrayList<>();

        for (City city : dataArray) {
            WarOfSquirrels.instance.debugLog("Hello '" + city.getDisplayName() + "' - '" + (city.getFaction() != null) + "'");
            if (city.getFaction() != null && city.getFaction().equals(faction)) {
                WarOfSquirrels.instance.debugLog("+1");
                cities.add(city);
            }
        }

        return cities;
    }

    public List<FullPlayer> getAssistants(City city) {
        List<FullPlayer> res = new ArrayList<>();

        for (FullPlayer player : city.getCitizens()) {
            if (player.getAssistant())
                res.add(player);
        }

        return res;
    }

    public List<String> getAssistants(UUID uuid) {
        List<FullPlayer> assistants = getAssistants(cityMap.get(uuid));
        List<String> res = new ArrayList<>();

        for (FullPlayer player : assistants) {
            res.add(player.getDisplayName());
        }

        return res;
    }

    public void SetCustomPermission(IPermission target, Permission permission, City city) {
        if (city.getCustomPermission().containsKey(target)) {
            city.getCustomPermission().replace(target, permission);
            city.getCustomPermissionList()
                    .stream().filter(e -> e.getTargetUuid().equals(target.getUuid()))
                    .findFirst().get().setPermission(permission);
        }
        else {
            city.getCustomPermission().put(target, permission);
            city.getCustomPermissionList().add(new CustomPermission(target.getUuid(), target.getPermissionTarget(), permission));
        }

        Save();
    }

    public void SetDefaultPermission(PermissionRelation relation, Permission permission, City city) {
        MutableComponent message = new TextComponent("Permission " + relation + " is now set to " + permission);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(city, null, message, true);
        city.getDefaultPermission().replace(relation, permission);
        Save();
    }

    public Collection<String> getAllAsCollection() {
        List<String> cities = new ArrayList<>();

        for (City city : dataArray)
            cities.add(city.getDisplayName());

        return cities;
    }

    public void updateDependencies() {
        for (City city : cityMap.values())
            city.updateDependencies();
        Save();
    }

    public void update() {
        for (City city : dataArray)
            city.Update();
    }
}
