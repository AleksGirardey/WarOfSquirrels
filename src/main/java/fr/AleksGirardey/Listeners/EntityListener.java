package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class EntityListener {
    private static List<EntityType>        types;

    static {
        types = new ArrayList<>();
        types.add(EntityTypes.BAT);
        types.add(EntityTypes.CAVE_SPIDER);
        types.add(EntityTypes.CREEPER);
        types.add(EntityTypes.ENDERMITE);
        types.add(EntityTypes.GHAST);
        types.add(EntityTypes.GIANT);
        types.add(EntityTypes.GUARDIAN);
        types.add(EntityTypes.MAGMA_CUBE);
        types.add(EntityTypes.PIG_ZOMBIE);
        types.add(EntityTypes.POLAR_BEAR);
        types.add(EntityTypes.SHULKER);
        types.add(EntityTypes.SILVERFISH);
        types.add(EntityTypes.SKELETON);
        types.add(EntityTypes.SPIDER);
        types.add(EntityTypes.WITCH);
        types.add(EntityTypes.ZOMBIE);
    }

    @Listener (order =  Order.FIRST)
    public void         onEntitySpawn(SpawnEntityEvent event) {
        event.getEntities().forEach(entity -> {
            if (types.contains(entity.getType()) &&
                    Core.getCuboHandler().get(entity.getLocation().getBlockPosition()) != null)
                event.setCancelled(true);
        });
    }
}
