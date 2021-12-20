package fr.craftandconquest.warofsquirrels.object.faction.city;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionTarget;
import fr.craftandconquest.warofsquirrels.object.war.AttackTarget;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class City implements IPermission, IFortification, IChannelTarget, AttackTarget {
    @AllArgsConstructor
    public static class CityCustomPermission {
        public UUID targetUuid;
        public CityCustomPermissionType type;
        public Permission permission;
    }

    public enum CityCustomPermissionType {
        Faction,
        City,
        Player,
    }

    @JsonProperty
    @Getter
    @Setter
    private UUID cityUuid;
    @Getter
    @Setter
    public String displayName;
    public String tag;
    public UUID ownerUUID;

    @JsonIgnore
    @Getter
    private Faction faction = null;
    @JsonProperty
    @Getter
    @Setter
    private UUID factionUuid;
    @Getter
    private CityRank rank;

    @JsonIgnore
    @Getter
    @Setter
    private Map<IPermission, Permission> customPermission = new HashMap<>();
    @Getter
    @Setter
    private Map<PermissionRelation, Permission> defaultPermission;
    @Getter
    @Setter
    private List<CityCustomPermission> customPermissionList = new ArrayList<>();

    @JsonIgnore
    private int balance;

    @JsonIgnore
    @Getter
    private FullPlayer owner;
    @JsonIgnore
    @Getter
    private final List<FullPlayer> citizens = new ArrayList<>();

    public List<FullPlayer> getAssistants() {
        return citizens.stream().filter(FullPlayer::getAssistant).collect(Collectors.toCollection(ArrayList::new));
    }

    @JsonIgnore
    public List<FullPlayer> getResidents() {
        return citizens.stream()
                .filter(FullPlayer::getResident)
                .filter(player -> !player.getAssistant())
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public List<FullPlayer> getRecruits() {
        return citizens.stream()
                .filter(player -> !player.getResident())
                .filter(player -> !player.getAssistant())
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public boolean addCitizen(FullPlayer player) {
        if (citizens.contains(player)) return false;

        citizens.add(player);
        player.setCity(this);

        MutableComponent messageToTarget = new TextComponent("Vous avez rejoint " + displayName + ".")
                .withStyle(ChatFormatting.GREEN);
        player.getPlayerEntity().sendMessage(messageToTarget, Util.NIL_UUID);

        MutableComponent messageToCity = new TextComponent(player.getDisplayName() + " à rejoint la ville.")
                .withStyle(ChatFormatting.GREEN);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null, messageToCity, true);
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(this, player);
        return true;
    }

    public boolean removeCitizen(FullPlayer player, boolean isKicked) {
        if (!citizens.contains(player)) return false;

        citizens.remove(player);
        player.setCity(null);

        player.getPlayerEntity().sendMessage(ChatText.Error(isKicked ?
                "Vous avez été expulsé de " + displayName + "." : "Vous avez quitté " + displayName), Util.NIL_UUID);

        MutableComponent messageToCity = ChatText.Error(player.getDisplayName() + (isKicked ?
                " a été expulsé de la ville." : " a quitté la ville."));

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null, messageToCity, true);
        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTarget(this, player);

        return true;
    }

    public void SetOwner(FullPlayer owner) {
        ownerUUID = owner != null ? owner.getUuid() : null;
        this.owner = owner;
    }

    public void SetRank(int rank) {
        this.rank = WarOfSquirrels.instance.getConfig().getCityRankMap().get(rank);
    }

    public void SetFaction(Faction faction) {
        this.faction = faction;
        if (faction != null)
            this.factionUuid = faction.getFactionUuid();
    }

    @Override
    public UUID getUuid() {
        return cityUuid;
    }

    @Override
    public PermissionTarget getPermissionTarget() {
        return PermissionTarget.CITY;
    }

    @Override
    public String getPermissionDisplayName() {
        return "C:" + getDisplayName();
    }

    @Override
    public BroadCastTarget getBroadCastTarget() {
        return BroadCastTarget.CITY;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", tag, displayName);
    }

    @Override
    public UUID getUniqueId() {
        return cityUuid;
    }

    @JsonIgnore
    public List<FullPlayer> getOnlinePlayers() {
        List<FullPlayer> onlinePlayers = new ArrayList<>();
        MinecraftServer server = WarOfSquirrels.server;

        if (server.getPlayerList().getPlayer(owner.getUuid()) != null)
            onlinePlayers.add(owner);

        for (FullPlayer player : citizens) {
            if (server.getPlayerList().getPlayer(player.getUuid()) != null)
                onlinePlayers.add(player);
        }

        return onlinePlayers;
    }

    public void displayInfo(FullPlayer player) {
        MutableComponent message = new TextComponent("");

        int size = WarOfSquirrels.instance.getChunkHandler().getSize(this);

        message.append("---===| " + rank.getName() + " " + displayName + " [" + citizens.size() + "] |===---\n");
        message.append("Faction: " + (faction == null ? "----" : faction.getDisplayName()) + "\n");
        message.append("Mayor: " + owner.getDisplayName() + "\n");
        message.append("Assistant(s): " + Utils.getStringFromPlayerList(getAssistants()) + "\n");
        message.append("Resident(s): " + Utils.getStringFromPlayerList(getCitizens()) + "\n");
        message.append("Recruit(s): " + Utils.getStringFromPlayerList(getRecruits()) + "\n");
        message.append("Tag: " + tag + "\n");
        message.append("Chunks [" + size + "/" + rank.chunkMax + "]\n");
        message.append("Outpost [" + WarOfSquirrels.instance.getChunkHandler().getOutpostSize(this) + "]\n");
        message.append("Permissions:\n" + displayPermissions());

        message.withStyle(ChatFormatting.BLUE);
        player.getPlayerEntity().sendMessage(message, Util.NIL_UUID);
    }

    public String displayPermissions() {
        StringBuilder permissionsAsString = new StringBuilder();

        permissionsAsString.append("=== Default Permission [Build|Container|Switch|Farm|Interact] ===\n");

        defaultPermission.forEach((k, v) ->
                permissionsAsString.append("    ").append(k.toString()).append(" ").append(v.toString()).append("\n"));

        permissionsAsString.append("=== Custom Permission [Build|Container|Switch|Farm|Interact] ===\n");

        customPermission.forEach((k, v) ->
                permissionsAsString.append("    ").append(k.getPermissionDisplayName()).append(" ").append(v.toString()).append("\n"));

        return permissionsAsString.toString();
    }

    public void updateDependencies() {
        SetOwner(WarOfSquirrels.instance.getPlayerHandler().get(ownerUUID));
        SetFaction(WarOfSquirrels.instance.getFactionHandler().get(factionUuid));

        for (CityCustomPermission permission : customPermissionList) {
            IPermission target = switch (permission.type) {
                case Faction -> WarOfSquirrels.instance.getFactionHandler().get(permission.targetUuid);
                case City -> WarOfSquirrels.instance.getCityHandler().getCity(permission.targetUuid);
                case Player -> WarOfSquirrels.instance.getPlayerHandler().get(permission.targetUuid);
            };

            if (target == null) continue;

            customPermission.put(target, permission.permission);
        }
    }
}
