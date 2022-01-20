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
    public int getSelfInfluenceGenerated(boolean gotAttacked, boolean gotDefeated) {
        int baseInfluence = gotAttacked ? 0 : 100;
        int upgrade = gotDefeated ? 0 : bastionUpgrade.getSelfInfluenceGenerated();

        return baseInfluence + upgrade;
    }

    @Override
    public int getInfluenceMax() {
        return bastionUpgrade.getInfluenceMax();
    }

    @Override
    public int getInfluenceGeneratedCloseNeighbour(boolean neutralOnly, boolean gotAttacked, boolean gotDefeated) {
        int baseInfluence = gotAttacked ? 0 : 50;
        int upgrade = gotDefeated ? 0 : bastionUpgrade.getInfluenceGeneratedCloseNeighbour(neutralOnly);

        return baseInfluence + upgrade;
    }

    @Override
    public int getInfluenceGeneratedDistantNeighbour(boolean gotAttacked, boolean gotDefeated) {
        int base = gotAttacked ? 0 : 0;
        int upgrade = gotDefeated ? 0 : bastionUpgrade.getInfluenceGeneratedDistantNeighbour();

        return base + upgrade;
    }

    @Override
    public int getInfluenceDamage(boolean gotAttacked, boolean gotDefeated) {
        if (gotDefeated) return 0;
        
        int barracksLevel = getBastionUpgrade().getBarrack().getCurrentLevel();
        
        return switch (barracksLevel) {
            case 1 -> 30;
            case 2 -> 35;
            case 3 -> 40;
            case 4 -> 45;
            default -> 0;
        };
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

        bastionUpgrade.Populate(this);

        if (upgradeChestLocation != null)
            upgradeChestLocation.update();
    }

    @Override
    public String toString() {
        return name + " " + territoryPosition;
    }
}
