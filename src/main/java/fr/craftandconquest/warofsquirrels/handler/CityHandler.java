package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.CustomPermission;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import fr.craftandconquest.warofsquirrels.object.upgrade.city.CityUpgrade;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;

public class CityHandler extends UpdatableHandler<City> {
    public CityHandler(Logger logger) {
        super("[WoS][CityHandler]", logger);
    }

    public City CreateCity(String name, String tag, FullPlayer owner, Territory territory) {
        City city = new City();

        city.setUuid(UUID.randomUUID());
        city.setDisplayName(name);
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

        return city;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Cities generated : {1}",
                PrefixLogger, dataArray.size()));
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {
        dataArray.forEach(city -> city.getCustomPermission().remove(target));
    }

    public City getCity(String cityName) {
        for (City city : dataArray) {
            if (city.getDisplayName().equals(cityName))
                return city;
        }
        return null;
    }

    @Override
    public boolean Delete(City city) {
        WarOfSquirrels.instance.spreadPermissionDelete(city);

        if (!WarOfSquirrels.instance.getInfluenceHandler().delete(city)) return false;
        if (!WarOfSquirrels.instance.getTerritoryHandler().delete(city)) return false;
        if (!WarOfSquirrels.instance.getBastionHandler().deleteCity(city)) return false;
        if (!WarOfSquirrels.instance.getCuboHandler().deleteCity(city)) return false;
        if (!WarOfSquirrels.instance.getChunkHandler().delete(city)) return false;

        city.getCitizens().forEach(this::RemoveCitizen);
        city.getOwner().setCity(null);

        super.Delete(city);

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
        player.setAssistant(false);
        player.setResident(false);
        player.setCity(null);
    }

    public List<City> getCities(Faction faction) {
        List<City> cities = new ArrayList<>();

        for (City city : dataArray) {
            if (city.getFaction() != null && city.getFaction().equals(faction)) {
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
        List<FullPlayer> assistants = getAssistants(get(uuid));
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
                    .findFirst().orElseThrow().setPermission(permission);
        } else {
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

    public void updateScore() {
        for (City city : dataArray)
            city.updateScore();
    }

    @Override
    protected String getDirName() {
        return super.getDirName() + "/Faction/City";
    }
}
