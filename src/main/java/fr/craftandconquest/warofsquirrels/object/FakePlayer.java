package fr.craftandconquest.warofsquirrels.object;

import net.minecraft.world.entity.player.Player;

public class FakePlayer extends FullPlayer {
    @Override
    public boolean isOnline() { return false; }

    @Override
    public Player getPlayerEntity() { return null; }
}
