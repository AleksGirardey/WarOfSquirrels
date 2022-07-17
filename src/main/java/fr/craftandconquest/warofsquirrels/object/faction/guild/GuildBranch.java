package fr.craftandconquest.warofsquirrels.object.faction.guild;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.IUpdate;
import fr.craftandconquest.warofsquirrels.object.faction.Upgradable;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class GuildBranch extends Upgradable implements IUpdate, IEstablishment {
    @JsonProperty @Getter @Setter private List<UUID> shopList = new ArrayList<>();
    @JsonProperty @Getter @Setter private UUID cityUuid;

    @JsonIgnore @Getter private List<GuildShop> shops = new ArrayList<>();
    @JsonIgnore @Getter private City city;

    @Override
    public void updateDependencies() {
        if (cityUuid != null) setCity(WarOfSquirrels.instance.getCityHandler().get(cityUuid));

        for (UUID uuid : shopList) {
            GuildShop shop = WarOfSquirrels.instance.getGuildShopHandler().get(uuid);
            if (shop == null || !AddShop(shop))
                WarOfSquirrels.instance.debugLog("Couldn't add shop to guild branch : " + uuid);
        }

        super.updateDependencies();
    }

    public void setCity(City city) {
        if (city != null) cityUuid = city.getUuid();
        this.city = city;
    }

    public boolean AddShop(GuildShop guildShop) {
        boolean added = false;
        if (!shopList.contains(guildShop.getUuid())) {
            shopList.add(guildShop.getUuid());
            added = true;
        }

        if (!shops.contains(guildShop)) {
            shops.add(guildShop);
            added = true;
        }

        return added;
    }

    @Override
    public void update() { }

    @Override
    public int Size() { return 0; }

    @Override
    public EstablishmentType getEstablishmentType() {
        return EstablishmentType.Branch;
    }

    @Override
    public List<IEstablishment> getSubEstablishment() {
        return shops.stream().map(shop -> (IEstablishment) shop).toList();
    }
}
