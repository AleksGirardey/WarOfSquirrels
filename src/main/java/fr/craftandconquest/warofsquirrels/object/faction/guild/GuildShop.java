package fr.craftandconquest.warofsquirrels.object.faction.guild;

import fr.craftandconquest.warofsquirrels.object.IUpdate;
import fr.craftandconquest.warofsquirrels.object.faction.Upgradable;

import java.util.Collections;
import java.util.List;

public class GuildShop extends Upgradable implements IUpdate, IEstablishment {
    @Override
    public void update() {

    }

    @Override
    public int Size() { return 0; }

    @Override
    public EstablishmentType getEstablishmentType() {
        return EstablishmentType.Shop;
    }

    @Override
    public List<IEstablishment> getSubEstablishment() {
        return Collections.emptyList();
    }
}