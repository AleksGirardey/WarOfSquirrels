package fr.craftandconquest.warofsquirrels.object.world;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public Chunk(double x, double z, String city, int dimensionId) {
        this(x, z);
        cityName = city;
        this.dimensionId = dimensionId;
        name = String.format("%s%d%d", cityName, posX, posZ);
    }

    public Chunk(double x, double z) {
        posX = (int) x;
        posZ = (int) z;
    }

    public Chunk() {}

    @JsonProperty("city") public String         getCityName() { return cityName; }
    @JsonProperty("city") public void           setCityName(String name) { cityName = name; }

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

    @Override
    public String toString() {
        return ("[" + posX + ";" + posZ + "] owned by " + cityName + " in dimension " + dimensionId);
    }
}
