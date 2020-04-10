package fr.craftandconquest.warofsquirrels.object.world;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Chunk {
    public int         posX;
    public int         posZ;
    private String      name;
    private Boolean     homeblock;
    private Boolean     outpost;
    private int         respawnX;
    private int         respawnY;
    private int         respawnZ;
    private String      cityName;
    private String      worldName;

    public Chunk(double x, double z, String city, String world) {
        this(x, z);
        cityName = city;
        worldName = world;
    }

    public Chunk(double x, double z) {
        posX = (int) x;
        posZ = (int) z;
    }

    public Chunk() {}

    //private City        city;
    //private World       world;

    @JsonProperty("city") public String   GetCityName() { return cityName; }
    @JsonProperty("city") public void     SetCityName(String name) { cityName = name; }

    @JsonProperty("world") public String   GetWorldName() { return worldName; }
    @JsonProperty("world") public void     SetWorldName(String name) { worldName = name; }

    public String   getName() { return name; }
    public void     setName(String name) { this.name = name; }

    public Boolean  getHomeblock() { return homeblock; }
    public void     setHomeblock(Boolean homeblock) { this.homeblock = homeblock; }

    public Boolean getOutpost() {
        return outpost;
    }

    public void setOutpost(Boolean outpost) {
        this.outpost = outpost;
    }

    public int getRespawnX() {
        return respawnX;
    }

    public void setRespawnX(int respawnX) {
        this.respawnX = respawnX;
    }

    public int getRespawnY() {
        return respawnY;
    }

    public void setRespawnY(int respawnY) {
        this.respawnY = respawnY;
    }

    public int getRespawnZ() {
        return respawnZ;
    }

    public void setRespawnZ(int respawnZ) {
        this.respawnZ = respawnZ;
    }

    @Override
    public String toString() {
        return ("[" + posX + ";" + posZ + "] owned by " + cityName + " in world " + worldName);
    }
}
