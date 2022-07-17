package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.RegistryObject;
import fr.craftandconquest.warofsquirrels.object.faction.city.ChestLocation;
import lombok.Getter;
import lombok.Setter;

public abstract class Upgradable extends RegistryObject {
    @JsonProperty @Getter @Setter protected ChestLocation upgradeChestLocation;

    public abstract int Size();

    @Override
    public void updateDependencies() {
        if (upgradeChestLocation != null)
            upgradeChestLocation.update();
    }
}
