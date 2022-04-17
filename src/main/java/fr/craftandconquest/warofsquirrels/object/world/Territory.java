package fr.craftandconquest.warofsquirrels.object.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.InfluenceHandler;
import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class Territory {

    @JsonProperty @Getter @Setter private UUID uuid;
    @JsonProperty @Getter @Setter private String name;
    @JsonProperty @Getter @Setter private int posX;
    @JsonProperty @Getter @Setter private int posZ;
    @JsonProperty @Getter         private UUID factionUuid;
    @JsonProperty @Getter         private UUID fortificationUuid;
    @JsonProperty @Getter @Setter private TerritoryBiome biome;
    @JsonProperty @Getter @Setter private boolean gotAttackedToday;
    @JsonProperty @Getter @Setter private boolean gotDefeatedToday;
    @JsonProperty @Getter @Setter private int daysBeforeReset;
    @JsonProperty @Getter @Setter private boolean hasFallen;

    @JsonIgnore @Getter private Faction faction;
    @JsonIgnore @Getter private IFortification fortification;

    public Territory(String name, int posX, int posZ, Faction faction, IFortification fortification) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.posX = posX;
        this.posZ = posZ;
        SetFaction(faction);
        SetFortification(fortification);

        SetBiomeMap();
    }

    public void SetFortification(IFortification fortification) {
        this.fortification = fortification;
        if (fortification != null)
            this.fortificationUuid = fortification.getUniqueId();
    }

    public void SetFaction(Faction faction) {
        this.faction = faction;
        if (faction != null)
            this.factionUuid = faction.getFactionUuid();
    }

    public void SpreadInfluence() {
        if (fortification != null) {
            if (faction == null) {
                SpreadSelfInfluence();
                return;
            }

            SpreadSelfInfluence();
            SpreadCloseInfluence(true);
            if (fortification.getFortificationType() == IFortification.FortificationType.BASTION) {
                SpreadCloseInfluence(false);
            }
            SpreadDistantInfluence();
        }
    }

    public void SpreadSelfInfluence() {
        int influence = fortification.getSelfInfluenceGenerated(gotAttackedToday, gotDefeatedToday);
        influence += (influence * biome.ratioBonusInfluenceOnMe());

        WarOfSquirrels.instance.getInfluenceHandler().pushInfluence(fortification, this, influence);
    }

    public void SpreadCloseInfluence(boolean neutralOnly) {
        InfluenceHandler handler = WarOfSquirrels.instance.getInfluenceHandler();
        List<Territory> neighbors = WarOfSquirrels.instance.getTerritoryHandler().getNeighbors(this);

        for (Territory territory : neighbors) {
            if (neutralOnly && territory.getFaction() != null) continue;

            int influence = fortification.getInfluenceGeneratedCloseNeighbour(neutralOnly, gotAttackedToday, gotDefeatedToday);
            influence += (influence * biome.ratioBonusInfluenceFromMe());
            influence += (influence * territory.getBiome().ratioBonusInfluenceOnMe());

            handler.pushInfluence(fortification, territory, influence);
        }
    }

    public void SpreadDistantInfluence() {
        InfluenceHandler handler = WarOfSquirrels.instance.getInfluenceHandler();
        List<Territory> neighbors = WarOfSquirrels.instance.getTerritoryHandler().getNeighbors(this, fortification.getInfluenceRange());

        for (Territory territory : neighbors) {
            if (territory.getFaction() == null) {
                int influence = fortification.getInfluenceGeneratedDistantNeighbour(gotAttackedToday, gotDefeatedToday);
                influence += (influence * biome.ratioBonusInfluenceFromMe());
                influence += (influence * territory.getBiome().ratioBonusInfluenceOnMe());

                handler.pushInfluence(fortification, territory, influence);
            }
        }
    }

    @JsonIgnore
    public int getInfluenceDamage() {
        return fortification.getInfluenceDamage(gotAttackedToday, gotDefeatedToday);
    }

    @JsonIgnore
    public int getInfluenceMax() {
        int base = 4000;
        int fromFortification = (fortification != null ? fortification.getInfluenceMax() : 0);
        int fromBiome = (int) biome.bonusInfluenceMax();

        return base + fromFortification + fromBiome;
    }

    private void SetBiomeMap() {
        Map<Biome.BiomeCategory, Integer> biomeMap = new HashMap<>();
        int territorySize = WarOfSquirrels.instance.getConfig().getTerritorySize();
        int posXMin = posX * territorySize;
        int posXMax = posXMin + territorySize;
        int posZMin = posZ * territorySize;
        int posZMax = posZMin + territorySize;

        ServerLevel level = WarOfSquirrels.server.getLevel(Level.OVERWORLD);

        if (level == null) return;

        for (int x = posXMin; x < posXMax; ) {
            for (int z = posZMin; z < posZMax; ) {
                ChunkPos chunkPos = Utils.FromWorldToChunkPos(x, z);
                BlockPos pos = chunkPos.getMiddleBlockPosition(124);

                Biome.BiomeCategory category = level.getBiome(pos).getBiomeCategory();
                biomeMap.compute(category, (k,v) -> v == null ? 1 : v + 1);

//                if (biomeMap.get(category) >= 335)
//                    WarOfSquirrels.instance.debugLog(category.getName() + " = " + biomeMap.get(category));

//                if (posX == 0 && posZ == 0) {
//                    WarOfSquirrels.instance.debugLog(category.getName() + " = " + biomeMap.get(category));
//                }

                z += 16;
            }
            x += 16;
        }

        biome = new TerritoryBiome(biomeMap);
    }

    public void update() {
        if (!hasFallen) {
            SpreadInfluence();
            gotAttackedToday = false;
            gotDefeatedToday = false;
        } else {
            --daysBeforeReset;

            if (daysBeforeReset <= 0) {
                WarOfSquirrels.instance.getBastionHandler().Delete((Bastion) fortification);
                hasFallen = false;
//                reset();
            }
        }
    }

    public void updateDependencies() {
        if (factionUuid != null)
            faction = WarOfSquirrels.instance.getFactionHandler().get(factionUuid);
        if (fortificationUuid != null) {
            fortification = WarOfSquirrels.instance.getCityHandler().getCity(fortificationUuid);
            if (fortification == null)
                fortification = WarOfSquirrels.instance.getBastionHandler().get(fortificationUuid);
        }
    }

    public void reset() {
        factionUuid = null;
        faction = null;
        fortificationUuid = null;
        fortification = null;
        gotAttackedToday = false;
        gotDefeatedToday = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        Territory territory = (Territory) obj;

        return territory.getPosX() == this.posX && territory.getPosZ() == this.posZ;
    }

    @Override
    public String toString() {
        return String.format("[%d;%d] Possédé par %s dans la dimension d'id %s",
                posX,
                posZ,
                faction != null ? faction.getDisplayName() : "personne", "Overworld");
    }

    @JsonIgnore
    public String getDisplayName() {
        String prefix = "";

        if (biome.isCompleteTrait()) prefix = " " + biome.getBiomePrefix();

        return name + prefix;
    }
}
