package fr.craftandconquest.warofsquirrels.object.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.faction.Guild;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class Chunk {
    @JsonProperty @Getter @Setter private int posX;
    @JsonProperty @Getter @Setter private int posZ;
    @JsonProperty @Getter @Setter private String name;
    @JsonProperty @Getter @Setter private Boolean homeBlock = false;
    @JsonProperty @Getter @Setter private Boolean guildHomeBlock = false;
    @JsonProperty @Getter @Setter private Boolean outpost = false;
    @JsonProperty @Getter @Setter private Boolean isGuild = false;
    @JsonProperty @Getter @Setter private Vector3 respawnPoint;
    @JsonProperty @Getter private String dimensionId;
    @JsonIgnore @Getter private ResourceKey<Level> dimension;

    @JsonProperty @Getter @Setter private UUID fortificationUuid;
    @JsonProperty @Getter @Setter private UUID guildUuid;
    @JsonIgnore @Getter private City city;
    @JsonIgnore @Getter private Bastion bastion;
    @JsonIgnore @Getter private Guild guild;

    public Chunk(double x, double z, UUID cityUuid, ResourceKey<Level> dimension) {
        this(x, z, WarOfSquirrels.instance.getCityHandler().getCity(cityUuid), dimension);
        name = String.format("%s%d%d", city.getDisplayName(), posX, posZ);
    }

    public Chunk(double x, double z, IFortification fortification, ResourceKey<Level> dimension) {
        posX = (int) x;
        posZ = (int) z;
        this.fortificationUuid = fortification.getUuid();
        setFortification();
        this.dimension = dimension;
        this.dimensionId = DimensionToId(dimension);
    }

    public Chunk() { }

    @JsonIgnore
    public IFortification getFortification() {
        return city == null ? bastion : city;
    }

    @JsonIgnore
    public void setFortification() {
        city = WarOfSquirrels.instance.getCityHandler().getCity(fortificationUuid);
        bastion = WarOfSquirrels.instance.getBastionHandler().get(fortificationUuid);
    }

    @JsonProperty
    public void setDimensionId(String id) {
        dimensionId = id;
        dimension = IdToDimension(id);
    }

    @JsonIgnore
    public void setDimension(ResourceKey<Level> dim) {
        dimension = dim;
        dimensionId = dim.location().getPath();
    }

    public void setGuild(Guild guild) {
        guildUuid = guild != null ? guild.getUuid() : null;
        this.guild = guild;
    }

    public void setCity(City city) {
        fortificationUuid = city != null ? city.getUuid() : null;
        this.city = city;
    }

    public void setBastion(Bastion bastion) {
        fortificationUuid = bastion != null ? bastion.getBastionUuid() : null;
        this.bastion = bastion;
    }

    public MutableComponent creationLogText() {
        StringBuilder message = new StringBuilder();

        message.append("[Chunk]");

        if (city != null) message.append(" The city ").append(String.format("'%s'", city.getDisplayName()));
        if (bastion != null) message.append(" The bastion ").append(String.format("'%s'", bastion.getDisplayName()));

        message.append(String.format(" has claim a new %s ", homeBlock ?
                        "HomeBlock" : (outpost ?
                        "Outpost" : "Chunk")))
                .append(name != null ? "'" + name + "' " : "")
                .append(String.format("at [%d;%d]", posX, posZ));

        if (homeBlock || outpost)
            message.append(String.format(" with respawn point at %s", respawnPoint));

        return ChatText.Success(message.toString());
    }

    public void setRespawn(BlockPos pos) {
        respawnPoint = new Vector3(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        Chunk chunk = (Chunk) obj;

        return chunk.posX == this.posX && chunk.posZ == this.posZ && chunk.getDimensionId().equals(this.dimensionId);
    }

    public String toStringShort() {
        return "[" + posX + ";" + posZ + "]";
    }

    @Override
    public String toString() {
        return ("[" + posX + ";" + posZ + "] owned by " + (city != null ? city.getDisplayName() : bastion.getDisplayName()) + " in dimension " + dimensionId);
    }

    public static ResourceKey<Level> IdToDimension(String id) {
        return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("minecraft", id));
    }

    public static String DimensionToId(ResourceKey<Level> dimension) {
        return dimension.location().getPath();
    }

    public void updateDependencies() {
        setFortification();
    }

    @JsonIgnore
    public City getRelatedCity() {
        if (city != null) return city;
        else if (bastion != null) return bastion.getRelatedCity();

        return null;
    }
}
