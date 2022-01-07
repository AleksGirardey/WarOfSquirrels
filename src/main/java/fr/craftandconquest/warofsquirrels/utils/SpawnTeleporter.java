package fr.craftandconquest.warofsquirrels.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class SpawnTeleporter implements ITeleporter {
    private final Vec3 dest;

    public SpawnTeleporter(int x, int y, int z) {
        dest = new Vec3(x, y, z);
    }

    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo)
    {
        return new PortalInfo(dest, Vec3.ZERO, entity.getYRot(), entity.getXRot());
    }
}
