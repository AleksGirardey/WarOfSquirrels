package fr.craftandconquest.warofsquirrels.object.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.UUID;

public class Chunk {
    public int posX;
    public int posZ;
    private String name;
    private Boolean homeBlock = false;
    private Boolean outpost = false;
    private int respawnX;
    private int respawnY;
    private int respawnZ;
    private UUID cityUuid;
    private int dimensionId;
    private ResourceKey<Level> dimension;

    private City city;

    public Chunk(double x, double z, UUID cityUuid, ResourceKey<Level> dimension) {
        this(x, z, WarOfSquirrels.instance.getCityHandler().getCity(cityUuid), dimension);
        name = String.format("%s%d%d", city.getDisplayName(), posX, posZ);
    }

    public Chunk(double x, double z, City city, ResourceKey<Level> dimension) {
        posX = (int) x;
        posZ = (int) z;
        this.cityUuid = city.getCityUuid();
        this.city = city;
        this.dimension = dimension;
        this.dimensionId = DimensionToId(dimension);
    }

    public Chunk() { }

    @JsonProperty("cityUuid")
    public UUID getCityUuid() {
        return cityUuid;
    }

    @JsonProperty("cityUuid")
    public void setCityUuid(UUID uuid) {
        cityUuid = uuid;
    }

    @JsonProperty("dimension")
    public int getDimensionId() {
        return dimensionId;
    }

    @JsonProperty("dimension")
    public void setDimensionId(int id) {
        dimensionId = id;
        dimension = IdToDimension(id);
    }

    @JsonIgnore void setDimension(ResourceKey<Level> dim) { dimension = dim; }

    @JsonIgnore
    public ResourceKey<Level> getDimension() { return dimension; }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("homeBlock")
    public Boolean getHomeBlock() {
        return homeBlock;
    }

    @JsonProperty("homeBlock")
    public void setHomeBlock(Boolean homeBlock) {
        this.homeBlock = homeBlock;
    }

    @JsonProperty("outpost")
    public Boolean getOutpost() {
        return outpost;
    }

    @JsonProperty("outpost")
    public void setOutpost(Boolean outpost) {
        this.outpost = outpost;
    }

    @JsonProperty("respawnPosX")
    public int getRespawnX() {
        return respawnX;
    }

    @JsonProperty("respawnPosX")
    public void setRespawnX(int respawnX) {
        this.respawnX = respawnX;
    }

    @JsonProperty("respawnPosY")
    public int getRespawnY() {
        return respawnY;
    }

    @JsonProperty("respawnPosY")
    public void setRespawnY(int respawnY) {
        this.respawnY = respawnY;
    }

    @JsonProperty("respawnPosZ")
    public int getRespawnZ() {
        return respawnZ;
    }

    @JsonProperty("respawnPosZ")
    public void setRespawnZ(int respawnZ) {
        this.respawnZ = respawnZ;
    }

    @JsonIgnore
    public City getCity() {
        return city;
    }

    @JsonIgnore
    public void setCity(City city) {
        this.city = city;
    }

    public MutableComponent creationLogText() {
        StringBuilder message = new StringBuilder();

        message.append("[Chunk] The city ")
                .append(String.format("'%s'", city.displayName))
                .append(String.format(" has claim a new %s ", homeBlock ?
                        "HomeBlock" : (outpost ?
                        "Outpost" : "Chunk")))
                .append(name != null ? "'" + name + "' " : "")
                .append(String.format("at [%d;%d]", posX, posZ));

        if (homeBlock || outpost)
            message.append(String.format(" with respawn point at [%d;%d;%d]", respawnX, respawnY, respawnZ));

        return ChatText.Success(message.toString());
    }

    public void setRespawn(BlockPos pos) {
        setRespawnX(pos.getX());
        setRespawnY(pos.getY());
        setRespawnZ(pos.getZ());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        Chunk chunk = (Chunk) obj;

        return chunk.posX == this.posX && chunk.posZ == this.posZ && chunk.getDimensionId() == this.dimensionId;
    }

    @Override
    public String toString() {
        return ("[" + posX + ";" + posZ + "] owned by " + city.getDisplayName() + " in dimension " + dimensionId);
    }

    public static ResourceKey<Level> IdToDimension(int id) {
        return switch (id) {
            case 0 -> Level.OVERWORLD;
            case 1 -> Level.NETHER;
            case 2 -> Level.END;
            default -> null;
        };
    }

    public static int DimensionToId(ResourceKey<Level> dimension) {
        if (dimension == Level.OVERWORLD) return 0;
        if (dimension == Level.NETHER) return 1;
        if (dimension == Level.END) return 2;

        return -1;
    }

    public void updateDependencies() {
        setCity(WarOfSquirrels.instance.getCityHandler().getCity(cityUuid));
    }
}
