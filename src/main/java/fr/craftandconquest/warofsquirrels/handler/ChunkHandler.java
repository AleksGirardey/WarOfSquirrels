package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.ChunkLocation;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkHandler extends Handler<Chunk> {

    private final Map<ChunkLocation, Chunk> chunksMap;

    public ChunkHandler(Logger logger) {
        super("[WoS][ChunkHandler]", logger);
        chunksMap = new HashMap<>();

        if (!Init()) return;
        if (!Load(new TypeReference<List<Chunk>>() {})) return;

        Log();
    }

    @Override
    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(chunk -> chunksMap.put(
                new ChunkLocation(chunk.posX, chunk.posZ, chunk.getDimensionId()),
                chunk));
        return true;
    }

    private void LogChunkCreation(Chunk chunk) {
        Logger.info(PrefixLogger + " Chunk created at " + chunk);
    }

    public boolean CreateChunk(Chunk chunk) {
        if (chunksMap.containsKey(chunk)) return false;

        ChunkLocation position = new ChunkLocation(chunk.posX, chunk.posZ, chunk.getDimensionId());

        chunksMap.put(position, chunk);
        Save(chunksMap.values());
        LogChunkCreation(chunk);
        return true;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Chunks generated : {1}",
                PrefixLogger, dataArray.size()));
    }

    public Chunk getChunk(int posX, int posZ, DimensionType dimensionType) {
        return chunksMap.getOrDefault(new ChunkLocation(posX, posZ, dimensionType.getId()), null);
    }

    public String getListAsString() {
        StringBuilder asString = new StringBuilder();

        for (Chunk c : dataArray) {
            asString.append(c).append("\n");
        }
        return asString.toString();
    }
}
