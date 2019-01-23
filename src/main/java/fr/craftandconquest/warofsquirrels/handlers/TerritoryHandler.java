package fr.craftandconquest.warofsquirrels.handlers;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalTerritory;
import fr.craftandconquest.warofsquirrels.objects.database.Statement;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Faction;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Territory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.world.World;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerritoryHandler {

    private Map<Integer, Territory>         territories = new HashMap<>();
    private Map<Faction, List<Territory>>   territoryMap = new HashMap<>();

    private int mapSize;
    private int territorySize;

    public      TerritoryHandler(World world) {
        mapSize = Core.getConfig().getMapSize();
        territorySize = Core.getConfig().getTerritorySize();
        if (!Core.getConfig().getTerritoriesGenerated()) {
            generate(world);
            Core.getConfig().setTerritoriesGenerated(true);
        }
    }

    public void     populate() {
        String      sql = "SELECT * FROM `" + GlobalTerritory.TABLENAME + "`";
        Territory   territory;

        try {
            Statement statement = new Statement(sql);
            statement.Execute();
            while (statement.getResult().next()) {
                territory = new Territory(statement.getResult());
                this.territories.put(territory.getId(), territory);
                if (!territoryMap.containsKey(territory.getFaction()))
                    this.territoryMap.put(territory.getFaction(), new ArrayList<>());
                this.territoryMap.get(territory.getFaction()).add(territory);
            }
        } catch (SQLException e) {
            Core.getLogger().error("Error populating Territories : " + e);
        }
    }

    private void             generate(World world) {
        int x = mapSize / territorySize;
        int z = mapSize / territorySize;

        x /= 2;
        z /= 2;

        for (int i = -z; i < z; i++) {
            for (int j = -x; j < x; j++) {
                Territory territory = new Territory("Province inconnue", i, j, null, world);
                this.add(territory);
            }
        }
    }

    public Territory        get(int id) { return territories.get(id); }
    public Territory        get(int posX, int posZ, World world) {
        for (Territory territory : territories.values()) {
            if (territory.getPosX() == posX && territory.getPosZ() == posZ
                    && territory.getWorld().getUniqueId().equals(world.getUniqueId()))
                return territory;
        }
        return null;
    }

    public List<Territory>  get(Faction faction) { return territoryMap.get(faction); }

    public void     add(Territory territory) {
        this.territories.put(territory.getId(), territory);
        if (!territoryMap.containsKey(territory.getFaction()))
            territoryMap.put(territory.getFaction(), new ArrayList<>());
        territoryMap.get(territory.getFaction()).add(territory);
    }

    public void     claim(int posX, int posZ, World world, Faction faction) {
        this.get(posX, posZ, world).setFaction(faction);
    }

    public void update() {
        for (Territory territory : territories.values())
            territory.spreadInfluence();
    }
}
