package fr.craftandconquest.warofsquirrels.handlers;

import fr.craftandconquest.warofsquirrels.objects.dbobject.Faction;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Territory;

public class InfluenceHandler {

    private Map<Faction, Map<Territory, Influence>> influenceMap;
    private Map<Integer, Influence>                 influences;
}