package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import javafx.util.Pair;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkHandler extends Handler<Chunk> {

    private final Map<Pair<Integer, Integer>, Chunk> chunksMap;

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
                new Pair<>(chunk.posX, chunk.posZ),
                chunk));
        return true;
    }

    private void LogChunkCreation(Chunk chunk) {
        Logger.info(PrefixLogger + " Chunk created at " + chunk);
    }

    public boolean CreateChunk(Chunk chunk) {
        Pair<Integer, Integer> position = new Pair<>(chunk.posX, chunk.posZ);
        if (chunksMap.containsKey(position)) return false;
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

    public Chunk get()

    public String getListAsString() {
        StringBuilder asString = new StringBuilder();

        for (Chunk c : dataArray) {
            asString.append(c).append("\n");
        }
        return asString.toString();
    }
}
