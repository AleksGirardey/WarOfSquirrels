package fr.craftandconquest.warofsquirrels.object.world;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.text.MessageFormat;

public class ChunkLocation {
    public int PosX;
    public int PosZ;
    public ResourceKey<Level> DimensionId;

    public ChunkLocation(Chunk chunk) {
        this(chunk.getPosX(), chunk.getPosZ(), chunk.getDimension());
    }

    public ChunkLocation(int posX, int posZ, ResourceKey<Level> dimensionId) {
        this.PosX = posX;
        this.PosZ = posZ;
        this.DimensionId = dimensionId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            WarOfSquirrels.LOGGER.error("[WoS][ERROR] Cannot equals ChunkLocation : NULL");
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            WarOfSquirrels.LOGGER.error("[WoS][ERROR] Cannot equals ChunkLocation : Wrong type");
            return false;
        }

        final ChunkLocation chunkLocation = (ChunkLocation) obj;

        return this.PosX == chunkLocation.PosX
                && this.PosZ == chunkLocation.PosZ;
//                && this.DimensionId.equals(chunkLocation.DimensionId);
    }

    public boolean equalsPosition(ChunkLocation chunkLocation) {
        return this.PosX == chunkLocation.PosX && this.PosZ == chunkLocation.PosZ;
    }

    @Override
    public String toString() {
        return MessageFormat.format("[{0};{1}][{2}]", PosX, PosZ, DimensionId);
    }
}
