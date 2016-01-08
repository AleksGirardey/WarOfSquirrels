package fr.AleksGirardey.Objects;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class Chunk {

    private int X;
    private int Z;

    public Chunk(Player player) {
        int     x = player.getLocation().getBlockX(),
                z = player.getLocation().getBlockZ();

        this.X = x / 16;
        this.Z = z / 16;
    }

    public Chunk(int x, int z) {
        this.X = x / 16;
        this.Z = z / 16;
    }

    public int getX()
    { return X; }

    public int getZ()
    { return Z; }

    public void setX(int x)
    { this.X = x; }

    public void setZ(int z)
    { this.Z = z; }

    public boolean equals(Chunk chunk)
    {
        return chunk.getX() == this.X && chunk.getZ() == this.Z;
    }
}
