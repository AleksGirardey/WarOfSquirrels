package fr.craftandconquest.warofsquirrels.object.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.city.City;

public class Chunk {
    public int         posX;
    public int         posZ;
    private String      name;
    private Boolean     homeBlock = false;
    private Boolean     outpost = false;
    private int         respawnX;
    private int         respawnY;
    private int         respawnZ;
    private String      cityName;
    private int         dimensionId;

    private City city;

    public Chunk(double x, double z, String city, int dimensionId) {
        this(x, z, WarOfSquirrels.instance.getCityHandler().getCity(city), dimensionId);
        name = String.format("%s%d%d", cityName, posX, posZ);
    }

    public Chunk(double x, double z, City city, int dimensionId) {
        posX = (int) x;
        posZ = (int) z;
        cityName = city.displayName;
        this.city = city;
        this.dimensionId = dimensionId;
    }

    public Chunk() {}

    @JsonProperty("cityName") public String         getCityName() { return cityName; }
    @JsonProperty("cityName") public void           setCityName(String name) { cityName = name; }

    @JsonProperty("dimension") public int       getDimensionId() { return dimensionId; }
    @JsonProperty("dimension") public void      setDimensionId(int id) { dimensionId = id; }

    @JsonProperty("name") public String         getName() { return name; }
    @JsonProperty("name") public void           setName(String name) { this.name = name; }

    @JsonProperty("homeBlock") public Boolean   getHomeBlock() { return homeBlock; }
    @JsonProperty("homeBlock") public void      setHomeBlock(Boolean homeBlock) { this.homeBlock = homeBlock; }

    @JsonProperty("outpost") public Boolean     getOutpost() { return outpost; }
    @JsonProperty("outpost") public void        setOutpost(Boolean outpost) { this.outpost = outpost; }

    @JsonProperty("respawnPosX") public int     getRespawnX() { return respawnX; }
    @JsonProperty("respawnPosX") public void    setRespawnX(int respawnX) { this.respawnX = respawnX; }

    @JsonProperty("respawnPosY") public int     getRespawnY() { return respawnY; }
    @JsonProperty("respawnPosY") public void    setRespawnY(int respawnY) { this.respawnY = respawnY; }

    @JsonProperty("respawnPosZ") public int     getRespawnZ() { return respawnZ; }
    @JsonProperty("respawnPosZ") public void    setRespawnZ(int respawnZ) { this.respawnZ = respawnZ; }

    @JsonIgnore public City getCity() { return city; }
    @JsonIgnore public void setCity(City city) { this.city = city; }

    private String creationLogText() {
        StringBuilder message = new StringBuilder();

        message.append("[Chunk] The city ")
                .append(String.format("'%s'", cityName))
                .append(String.format(" has claim a new %s ", homeBlock ?
                        "HomeBlock" : (outpost ?
                        "Outpost" : "Chunk")))
                .append(name != null ? "'"+ name + "' " : "")
                .append(String.format("at [%d;%d]", posX, posZ));

        if (homeBlock || outpost)
            message.append(String.format(" with respawn point at [%d;%d;%d]", respawnX, respawnY, respawnZ));

        return message.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Chunk) {
            Chunk chunk = (Chunk) obj;
            return chunk.posX == posX && chunk.posZ == posZ && chunk.getDimensionId() == dimensionId;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return ("[" + posX + ";" + posZ + "] owned by " + cityName + " in dimension " + dimensionId);
    }
}
