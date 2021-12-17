package fr.craftandconquest.warofsquirrels.utils;

import lombok.AllArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

@AllArgsConstructor
public class ReSpawnPoint {
    public ResourceKey<Level> dimension;
    public BlockPos position;
}