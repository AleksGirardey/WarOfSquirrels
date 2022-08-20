package fr.craftandconquest.warofsquirrels.object.admin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.RegistryObject;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.object.upgrade.UpgradeItem;
import lombok.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class CustomReward extends RegistryObject {
    @JsonProperty @Getter @Setter protected int rewardedQuantity;
    @JsonProperty @Getter @Setter protected UpgradeItem reward;
    @JsonProperty @Getter @Setter protected int rewardQuantity;
    @JsonProperty @Getter @Setter private UUID adminCuboUuid;
    @JsonProperty private List<UUID> claimedPlayerUuids;
    @JsonProperty private List<UUID> rewardedPlayerUuids;

    @JsonIgnore @Getter @Setter protected AdminCubo adminCubo;
    @JsonIgnore @Getter @Setter private List<FullPlayer> rewardedPlayers = new ArrayList<>();
    @JsonIgnore @Getter @Setter private List<FullPlayer> claimedPlayers = new ArrayList<>();

    @JsonCreator
    public CustomReward(
            @JsonProperty("adminCuboUuid") UUID _adminCuboUuid,
            @JsonProperty("rewardedQuantity") int _rewardedQuantity,
            @JsonProperty("reward") UpgradeItem _reward,
            @JsonProperty("rewardQuantity") int _rewardQuantity,
            @JsonProperty("claimedPlayerUuids") List<UUID> _claimedPlayerUuids,
            @JsonProperty("rewardedPlayerUuids") List<UUID> _rewardedPlayerUuids) {
        adminCuboUuid = _adminCuboUuid;
        rewardedQuantity = _rewardedQuantity;
        reward = _reward;
        rewardQuantity = _rewardQuantity;
        claimedPlayerUuids = _claimedPlayerUuids;
        rewardedPlayerUuids = _rewardedPlayerUuids;

        updateDependencies();
    }

    public boolean CanAddRewardedPlayer() {
        return rewardedPlayers.size() < rewardedQuantity;
    }

    public boolean AddRewardedPlayer(FullPlayer player) {
        if (rewardedPlayers.contains(player)) return false;

        rewardedPlayers.add(player);
        rewardedPlayerUuids.add(player.getUuid());

        return true;
    }

    public boolean claim(FullPlayer player) {
        if (rewardedPlayers.contains(player)) {
            claimedPlayers.add(player);
            claimedPlayerUuids.add(player.getUuid());

            dropReward(player);

            return true;
        }
        return false;
    }

    public void dropReward(FullPlayer player) {
        ItemStack stack = new ItemStack(reward.getItem(), rewardQuantity);
        ServerPlayer serverPlayer = (ServerPlayer) player.getPlayerEntity();
        ItemEntity entity = serverPlayer.drop(stack, false);

        if (entity != null) {
            entity.setNoPickUpDelay();
            entity.setOwner(serverPlayer.getUUID());
        }
    }

    public void Reset() {
        claimedPlayerUuids.clear();
        claimedPlayers.clear();
        rewardedPlayerUuids.clear();
        rewardedPlayers.clear();
    }

    @Override
    public void updateDependencies() {
        adminCubo = WarOfSquirrels.instance.getAdminHandler().get(adminCuboUuid);
        for (UUID uuid : claimedPlayerUuids) claimedPlayers.add(WarOfSquirrels.instance.getPlayerHandler().get(uuid));
        for (UUID uuid : rewardedPlayerUuids) {
            FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(uuid);

            rewardedPlayers.add(player);
            if (!claimedPlayerUuids.contains(uuid))
                player.getRewards().add(this);
        }
    }
}
