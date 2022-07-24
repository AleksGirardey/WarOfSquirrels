package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.IUpdate;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.scoring.Score;
import fr.craftandconquest.warofsquirrels.object.upgrade.bastion.BastionUpgrade;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class Bastion extends Upgradable implements IFortification, IUpdate {
    @JsonProperty @Setter
    private Vector2 territoryPosition;

    @JsonProperty @Getter
    private UUID cityUuid;

    @JsonProperty @Setter
    private boolean isProtected;

    @JsonProperty @Getter @Setter
    private BastionUpgrade bastionUpgrade;

    @JsonProperty @Setter private Score score = new Score();

    @JsonIgnore @Getter
    private City city;

    public void setCity(City city) {
        this.city = city;
        if (city != null)
            this.cityUuid = city.getUuid();
    }

    @Override
    public Faction getFaction() {
        return this.city.getFaction();
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

    @JsonIgnore
    public int getCostReduction() {
        Territory target = WarOfSquirrels.instance.getTerritoryHandler().get(this);
        int base = 0;
        int territory = target != null ? (int) target.getBiome().ratioUpgradeReduction() : 0;

        return base + territory;
    }

    @Override
    public void update() {
        isProtected = false;
        bastionUpgrade.VerifyLevelUp();
    }

    @Override
    public int Size() {
        return 0;
    }

    @Override
    public void updateDependencies() {
        city = WarOfSquirrels.instance.getCityHandler().get(cityUuid);

        bastionUpgrade.Populate(this);

        if (upgradeChestLocation != null)
            upgradeChestLocation.update();
    }

    @Override
    public Vector2 getTerritoryPosition() {
        return territoryPosition;
    }

    @Override
    public String toString() {
        return displayName + " " + territoryPosition;
    }

    @Override
    public void displayInfo(FullPlayer player) {
        MutableComponent message = new TextComponent("");

        int size = WarOfSquirrels.instance.getChunkHandler().getSize(this);

        message.append("--==| " + getDisplayName() + " |==--\n");
        message.append("  Chunks [" + size + "/" + getMaxChunk() + "]");
        message.append("  Score (Upcoming):" + score);
        message.append("  Score life points:" + score.getScoreLifePoints() + "/800");
        message.append("\n -= Upgrades =-\n");
        message.append(bastionUpgrade.displayInfo());
    }

    @Override
    public void updateScore() {
        score.AddScoreLifePoints(100);
        score.UpdateScore();
    }

    @JsonIgnore @Override
    public Score getScore() { return score; }

    @Override
    public String getDisplayName() { return displayName; }
}
