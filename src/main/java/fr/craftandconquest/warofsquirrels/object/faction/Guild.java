package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.*;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

import java.util.*;

public class Guild extends Upgradable implements IPermission, IChannelTarget {
    @JsonProperty @Getter @Setter private String tag;
    @JsonProperty @Getter @Setter private UUID ownerUuid;
    @JsonProperty @Getter @Setter private UUID cityHeadQuarterUuid;

    @JsonProperty @Getter @Setter private Map<PermissionRelation, Permission> defaultPermission;
    @JsonProperty @Getter @Setter private List<CustomPermission> customPermissionList = new ArrayList<>();

    // Data not registered
    @JsonIgnore @Getter private FullPlayer owner;
    @JsonIgnore @Getter private City cityHeadQuarter;
    @JsonIgnore @Getter @Setter private List<FullPlayer> members = new ArrayList<>();
    @JsonIgnore @Getter @Setter private Map<IPermission, Permission> customPermission = new HashMap<>();

    @Override
    public PermissionTarget getPermissionTarget() {
        return PermissionTarget.GUILD;
    }

    @Override
    public String getPermissionDisplayName() {
        return getDisplayName();
    }

    @Override
    public String displayPermissions() {
        StringBuilder permissionsAsString = new StringBuilder();

        permissionsAsString.append("== Default Permission [Build|Container|Switch|Farm|Interact] ==\n");

        defaultPermission.forEach((k, v) ->
                permissionsAsString.append("  ").append(k.toString()).append(" ").append(v.toString()).append("\n"));

        permissionsAsString.append("== Custom Permission [Build|Container|Switch|Farm|Interact] ==\n");

        customPermission.forEach((k, v) ->
                permissionsAsString.append("  ").append(k.getPermissionDisplayName()).append(" ").append(v.toString()).append("\n"));

        return permissionsAsString.toString();
    }

    @Override
    public EPermissionType getPermissionType() {
        return IPermission.EPermissionType.GUILD;
    }

    @JsonIgnore
    public boolean addMember(FullPlayer player) {
        if (!register(player)) return false;

        MutableComponent messageToTarget = new TextComponent("You joined guild '" + displayName + "'.")
                .withStyle(ChatFormatting.GREEN);
        MutableComponent messageToGuild = new TextComponent(player.getDisplayName() + " joined the guild.")
                .withStyle(ChatFormatting.GREEN);

        player.sendMessage(messageToTarget);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null, messageToGuild, true);
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(this, player);

        return true;
    }

    @JsonIgnore
    public boolean removeMember(FullPlayer player, boolean isKicked) {
        if (!members.contains(player)) return false;

        members.remove(player);
        player.setGuild(null);
        //player.resetGuild();

        player.sendMessage(ChatText.Error((isKicked ? "You got expelled from guild " : "You left guild ") + displayName));

        MutableComponent messageToGuild = ChatText.Error(player.getDisplayName() + (isKicked ?
                " got expelled from the guild." : " left the guild."));

        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTarget(this, player);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null, messageToGuild, true);
        return true;
    }

    public boolean register(FullPlayer player) {
        if (members.contains(player)) return false;

        members.add(player);
        player.setGuild(this);

        return true;
    }

    @Override
    public BroadCastTarget getBroadCastTarget() {
        return BroadCastTarget.GUILD;
    }

    @Override
    public void updateDependencies() {
        if (cityHeadQuarterUuid != null) setCityHeadQuarter(WarOfSquirrels.instance.getCityHandler().getCity(cityHeadQuarterUuid));
        if (ownerUuid != null) setOwner(WarOfSquirrels.instance.getPlayerHandler().get(ownerUuid));

        for (CustomPermission permission : customPermissionList) {
            IPermission target = permission.getTarget();

            if (target == null) continue;

            customPermission.put(target, permission.permission);
        }

        //guildUpgrade.Populate(this);

        super.updateDependencies();
    }

    @JsonIgnore
    public Chunk getHomeBlock() { return WarOfSquirrels.instance.getChunkHandler().getHomeBlock(this); }

    @Override
    public String toString() { return String.format("[Guild][%s] %s", tag, displayName); }

    // Setter

    @JsonIgnore
    public void setOwner(FullPlayer target) {
        ownerUuid = target != null ? target.getUuid() : null;
        owner = target;
    }

    @JsonIgnore
    public void setCityHeadQuarter(City city) {
        cityHeadQuarterUuid = (city != null ? city.getUuid() : null);
        cityHeadQuarter = city;
    }
}
