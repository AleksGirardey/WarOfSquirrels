package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.admin.CustomReward;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RewardHandler extends Handler<CustomReward> {
    public RewardHandler(Logger logger) {
        super("[WoS][RewardHandler]", logger);
    }

    @Override
    protected void InitVariables() {}

    @Override
    protected void CustomLoad(File configFile) throws IOException {
        dataArray = jsonArrayToList(configFile, CustomReward.class);
    }

    @Override
    public void Log() {
        WarOfSquirrels.instance.debugLog(this.PrefixLogger + "Rewards created: " + dataArray.size());
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {}

    @Override
    public boolean add(CustomReward value) {
        return super.add(value);
    }

    public List<CustomReward> getAll(ResourceKey<Level> dimensionKey) {
        List<CustomReward> list = new ArrayList<>();

        for (CustomReward reward : dataArray) {
            if (reward.getAdminCubo().getDimensionKey().equals(dimensionKey))
                list.add(reward);
        }

        return list;
    }

    public CustomReward contains(Vector3 click) {
        for (CustomReward reward : dataArray) {
            if (reward.getAdminCubo().getVector().contains(click))
                return reward;
        }

        return null;
    }

    public int claim(FullPlayer player) {
        int claimCount = 0;
        List<CustomReward> rewards = player.getRewards();

        for (CustomReward reward : rewards) {
            if (reward.claim(player))
                ++claimCount;
        }

        player.getRewards().clear();
        return claimCount;
    }
}
