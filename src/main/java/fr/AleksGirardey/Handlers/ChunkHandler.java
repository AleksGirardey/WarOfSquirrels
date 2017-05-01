package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Database.GlobalChunk;
import fr.AleksGirardey.Objects.Database.Statement;
import fr.AleksGirardey.Objects.Utilitaires.ConfigLoader;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import org.slf4j.Logger;
import org.spongepowered.api.world.World;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChunkHandler {

    private Map<Integer, Chunk>         chunks = new HashMap<>();
    private Map<City, List<Chunk>>      chunkMap = new HashMap<>();

    private Logger                      logger;

    public      ChunkHandler(Logger logger) {
        this.logger = logger;
    }

    public void    populate() {
        String      sql = "SELECT * FROM `" + GlobalChunk.tableName + "`;";
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

    public Chunk            get(int posX, int posZ, World world) {
        for (Chunk chunk : chunks.values())
            if (chunk.getPosX() == posX
                    && chunk.getPosZ() == posZ
                    && chunk.getWorld() == world)
                return chunk;
        return null;
    }

    public List<Chunk>      get(City city) { return chunkMap.get(city); }

    public boolean          exists(int posX, int posZ, World world){
        for (Chunk chunk : chunks.values())
            if (chunk.getPosX() == posX
                    && chunk.getPosZ() == posZ
                    && chunk.getWorld() == world)
                return true;
        return false;
    }

    public boolean          exists(Chunk chunk) { return exists(chunk.getPosX(), chunk.getPosZ(), chunk.getWorld()); }

    public void             add(Chunk chunk) {
        this.chunks.put(chunk.getId(), chunk);
        if (!chunkMap.containsKey(chunk.getCity()))
            chunkMap.put(chunk.getCity(), new ArrayList<>());
        this.chunkMap.get(chunk.getCity()).add(chunk);
    }

    public void             delete(Chunk chunk) {
        this.chunks.remove(chunk.getId());
        this.chunkMap.get(chunk.getCity()).remove(chunk);
        chunk.delete();
    }

    public void             deleteCity(City city) {
        for (Chunk c : chunkMap.get(city)) {
            chunks.remove(c.getId());
            c.delete();
        }
        chunkMap.remove(city);
    }

    public boolean      canBePlaced(City city, int posX, int posZ, boolean outpost, World world) {
        if (outpost)
            return Utils.CanPlaceOutpost(posX, posZ);

        return (Core.getWorld() == world && (get(posX + 1, posZ, world) != null && get(posX + 1, posZ, world).getCity() == city) ||
                (get(posX - 1, posZ, world) != null && get(posX - 1, posZ, world).getCity() == city) ||
                (get(posX, posZ + 1, world) != null && get(posX, posZ + 1, world).getCity() == city) ||
                (get(posX, posZ - 1, world) != null && get(posX, posZ - 1, world).getCity() == city));
    }

    public List<Chunk>  getHomeblockList() {
        return chunks.values().stream().filter(Chunk::isHomeblock).collect(Collectors.toList());
    }

    public List<Chunk>  getOupostList(City city) {
        return chunkMap.get(city).stream().filter(Chunk::isOutpost).collect(Collectors.toList());
    }


    public int getSize(City city) { return (chunkMap.get(city).size() - getOupostList(city).size()); }

    public int getOutpostSize(City city) { return getOupostList(city).size(); }

    public Chunk getHomeblock(City city) {
        for (Chunk c : chunkMap.get(city))
            if (c.isHomeblock())
                return c;
        return null;
    }

    public void         setHomeblock(DBPlayer player) {
        Chunk           homeblock;
        City            city;

        homeblock = get(player.getLastChunkX(), player.getLastChunkZ(), player.getUser().getPlayer().get().getWorld());
        if (homeblock.getCity() == player.getCity())
            city = player.getCity();
        else
            city = homeblock.getCity();

        homeblock.setHomeblock(true);
        getHomeblock(city).setHomeblock(false);
    }
}