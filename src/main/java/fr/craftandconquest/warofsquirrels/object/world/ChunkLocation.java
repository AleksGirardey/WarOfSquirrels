package fr.craftandconquest.warofsquirrels.object.world;

import java.text.MessageFormat;

public class ChunkLocation {
    public int PosX;
    public int PosZ;
    public int DimensionId;

    public ChunkLocation(int posX, int posZ, int dimensionId) {
        this.PosX = posX;
        this.PosZ = posZ;
        this.DimensionId = dimensionId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChunkLocation) {
            ChunkLocation chunkLocation = (ChunkLocation) obj;

            return this.PosX == chunkLocation.PosX
                    && this.PosZ == chunkLocation.PosZ
                    && this.DimensionId == chunkLocation.DimensionId;
        } else if (obj instanceof Chunk) {
            Chunk chunk = (Chunk) obj;

            return this.PosX == chunk.posX
                    && this.PosZ == chunk.posZ
                    && this.DimensionId == chunk.getDimensionId();
        }
        return super.equals(obj);
    }

    public boolean equalsPosition(ChunkLocation chunkLocation) {
        return this.PosX == chunkLocation.PosX && this.PosZ == chunkLocation.PosZ;
    }

    @Override
    public String toString() {
        return MessageFormat.format("[{0};{1}][{2}]", PosX, PosZ, DimensionId);
    }
}
