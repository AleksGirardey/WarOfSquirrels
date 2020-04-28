package fr.craftandconquest.warofsquirrels.utils;
import lombok.AllArgsConstructor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

@AllArgsConstructor
public class ReSpawnPoint {
    public DimensionType dimension;
    public BlockPos position;
}