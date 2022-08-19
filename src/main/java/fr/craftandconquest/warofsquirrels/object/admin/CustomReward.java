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

    public void AddRewardedPlayer(FullPlayer player) {
        rewardedPlayerUuids.add(player.getUuid());
        rewardedPlayers.add(player);
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
        for (UUID uuid : rewardedPlayerUuids) rewardedPlayers.add(WarOfSquirrels.instance.getPlayerHandler().get(uuid));
        for (UUID uuid : claimedPlayerUuids) claimedPlayers.add(WarOfSquirrels.instance.getPlayerHandler().get(uuid));
    }
}
