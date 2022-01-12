package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.city.ChestLocation;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.upgrade.BastionUpgrade;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.network.chat.MutableComponent;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class Bastion implements IFortification {
    @JsonProperty @Getter @Setter
    private UUID bastionUuid;

    @JsonProperty @Getter @Setter
    private String name;

    @JsonProperty @Getter @Setter
    private Vector2 territoryPosition;

    @JsonProperty @Getter
    private UUID cityUuid;

    @JsonProperty @Setter
    private boolean isProtected;

    @JsonProperty @Getter @Setter
    private BastionUpgrade bastionUpgrade;

    @JsonProperty @Getter @Setter private ChestLocation upgradeChestLocation;

    @JsonIgnore @Getter
    private City city;

    public void SetCity(City city) {
        this.city = city;
        if (city != null)
            this.cityUuid = city.getCityUuid();
    }

    @Override
    public Faction getFaction() {
        return this.city.getFaction();
    }

    @Override
    public String getDisplayName() { return name; }

    @Override
    public UUID getUniqueId() {
        return bastionUuid;
    }

    @Override
    public City getRelatedCity() {
        return city;
    }

    @Override
    public FortificationType getFortificationType() {
        return FortificationType.BASTION;
    }

    @Override
    public int getSelfInfluenceGenerated(boolean gotAttacked) {
        int baseInfluence = gotAttacked ? 0 : 100;
        return baseInfluence + bastionUpgrade.getSelfInfluenceGenerated();
    }

    @Override
    public int getInfluenceMax() {
        return 4000 + bastionUpgrade.getInfluenceMax();
    }

    @Override
    public int getInfluenceGeneratedCloseNeighbour(boolean neutralOnly, boolean gotAttacked) {
        int baseInfluence = gotAttacked ? 0 : 50;
        return baseInfluence + bastionUpgrade.getInfluenceGeneratedCloseNeighbour(neutralOnly);
    }

    @Override
    public int getInfluenceGeneratedDistantNeighbour(boolean gotAttacked) {
        return 0 + bastionUpgrade.getInfluenceGeneratedDistantNeighbour();
    }

    @Override
    public int getInfluenceRange() {
        return 0;
    }

    @JsonIgnore
    public Chunk getHomeBlock() {
        return WarOfSquirrels.instance.getChunkHandler().getHomeBlock(this);
    }

    @JsonIgnore @Override
    public Vector3 getSpawn() {
        return getHomeBlock().getRespawnPoint();
    }

    @JsonIgnore @Override
    public boolean isProtected() { return isProtected; }

    @Override
    public int getMaxChunk() {
        return bastionUpgrade.getMaxChunk();
    }

    @Override
    public int getLinkedChunkSize() {
        return WarOfSquirrels.instance.getChunkHandler().getChunks(this).size();
    }

    public void update() {
        isProtected = false;
        bastionUpgrade.VerifyLevelUp();
    }

    public void updateDependencies() {
        city = WarOfSquirrels.instance.getCityHandler().getCity(cityUuid);

        if (upgradeChestLocation != null)
            upgradeChestLocation.update();
    }

    @Override
    public String toString() {
        return name + " " + territoryPosition;
    }
}
