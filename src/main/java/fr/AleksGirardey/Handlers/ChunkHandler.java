package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.Database.GlobalChunk;
import fr.AleksGirardey.Objects.Database.Statement;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChunkHandler {

    private Map<Integer, Chunk>         chunks;
    private Map<City, List<Chunk>>      chunkMap;

    private Logger                      logger;

    public      ChunkHandler(Logger logger) {
        this.logger = logger;
        this.populate();
    }

    private void    populate() {
        String      sql = "SELET * FROM `" + GlobalChunk.tableName + "`;";
        Chunk       chunk;

        try {
            Statement   statement = new Statement(sql);
            statement.Execute();
            while (statement.getResult().next()) {
                chunk = new Chunk(statement.getResult());
                this.chunks.put(chunk.getId(), chunk);
                if (!chunkMap.containsKey(chunk.getCity()))
                    this.chunkMap.put(chunk.getCity(), new ArrayList<>());
                this.chunkMap.get(chunk.getCity()).add(chunk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Chunk            get(int id) { return chunks.get(id); }

    public Chunk            get(int posX, int posZ) {
        for (Chunk chunk : chunks.values())
            if (chunk.getPosX() == posX
                    && chunk.getPosZ() == posZ)
                return chunk;
        return null;
    }

    public List<Chunk>      get(City city) { return chunkMap.get(city); }

    public boolean          exists(int posX, int posZ){
        for (Chunk chunk : chunks.values())
            if (chunk.getPosX() == posX
                    && chunk.getPosZ() == posZ)
                return true;
        return false;
    }

    public boolean          exists(Chunk chunk) { return exists(chunk.getPosX(), chunk.getPosZ()); }

    public void             add(Chunk chunk) {
        this.chunks.put(chunk.getId(), chunk);
        if (!chunkMap.containsKey(chunk.getCity()))
            chunkMap.put(chunk.getCity(), new ArrayList<>());
        this.chunkMap.get(chunk.getCity()).add(chunk);
    }

    public void             delete(Chunk chunk) {
        this.chunks.remove(chunk.getId());
        this.chunkMap.get(chunk.getCity()).remove(chunk);
    }

    public void             deleteCity(City city) {
        for (Chunk c : chunkMap.get(city))
            chunks.remove(c.getId());
        chunkMap.remove(city);
    }

    public boolean      canBePlaced() { return false; }
/*
    public boolean canBePlaced(int cityId, int x, int z, boolean b) {
        if (b) {
            Core.Send("ALLO");
            Chunk chunk = new Chunk(0, 0);
            chunk.setX(x);
            chunk.setZ(z);
            return Utils.NearestHomeblock(chunk) <= ConfigLoader.distanceOutpost;
        }
        Core.Send("Check One : " + (x + 1) + ";" + z + " => " + getCity(x + 1, z) + " vs " + cityId);
        Core.Send("Check One : " + (x - 1) + ";" + z + " => " + getCity(x - 1, z) + " vs " + cityId);
        Core.Send("Check One : " + x + ";" + (z + 1) + " => " + getCity(x , z + 1) + " vs " + cityId);
        Core.Send("Check One : " + x + ";" + (z - 1) + " => " + getCity(x , z - 1) + " vs " + cityId);
        return (getCity(x + 1, z) == cityId
                || getCity(x - 1, z) == cityId
                || getCity(x, z + 1) == cityId
                || getCity(x, z - 1) == cityId);
    } */

    public List<Chunk>  getHomeblockList() {
        List<Chunk>     list = new ArrayList<>();

        for (Chunk c : chunks.values())
            if (c.isHomeblock())
                list.add(c);
        return list;
    }

    public List<Chunk>  getOupostList(City city) {
        return chunkMap.get(city).stream().filter(c -> c.isOutpost()).collect(Collectors.toList());
    }


    public int getSize(City city) { return (chunkMap.get(city).size() - getOupostList(city).size()); }

    public int getOutpostSize(City city) { return getOupostList(city).size(); }
}