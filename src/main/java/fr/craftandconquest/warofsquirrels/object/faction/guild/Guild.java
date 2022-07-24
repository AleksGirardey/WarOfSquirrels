package fr.craftandconquest.warofsquirrels.object.faction.guild;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.IUpdate;
import fr.craftandconquest.warofsquirrels.object.faction.Upgradable;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.*;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;

public class Guild extends Upgradable implements IPermission, IChannelTarget, IUpdate, IEstablishment {
    @JsonProperty @Getter @Setter private String tag;
    @JsonProperty @Getter @Setter private UUID ownerUuid;
    @JsonProperty @Getter @Setter private UUID cityHeadQuarterUuid;
    @JsonProperty @Getter @Setter private List<UUID> branchListUuid = new ArrayList<>();

    @JsonProperty @Getter @Setter private Map<PermissionRelation, Permission> defaultPermission = new HashMap<>();
    @JsonProperty @Getter @Setter private List<CustomPermission> customPermissionList = new ArrayList<>();

    // Data not registered
    @JsonIgnore @Getter private FullPlayer owner;
    @JsonIgnore @Getter private City cityHeadQuarter;
    @JsonIgnore @Getter private final List<GuildBranch> branches = new ArrayList<>();
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

        MutableComponent messageToTarget = ChatText.Colored("You joined guild '" + displayName + "'.", ChatFormatting.GREEN);
        MutableComponent messageToGuild = ChatText.Colored(player.getDisplayName() + " joined the guild.", ChatFormatting.GREEN);

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

    @JsonIgnore
    public boolean AddBranch(GuildBranch branch) {
        boolean added = false;
        if (!branchListUuid.contains(branch.getUuid())) {
            branchListUuid.add(branch.getUuid());
            added = true;
        }

        if (!branches.contains(branch)) {
            branches.add(branch);
            added = true;
        }

        return added;
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
    public int Size() {
        return getMembers().size();
    }

    @Override
    public void updateDependencies() {
        if (cityHeadQuarterUuid != null) setCityHeadQuarter(WarOfSquirrels.instance.getCityHandler().get(cityHeadQuarterUuid));
        if (ownerUuid != null) setOwner(WarOfSquirrels.instance.getPlayerHandler().get(ownerUuid));

        for (UUID branchUuid : branchListUuid) {
            GuildBranch branch = WarOfSquirrels.instance.getGuildBranchHandler().get(branchUuid);
            if (branch == null || !AddBranch(branch))
                WarOfSquirrels.instance.debugLog("Couldn't add guild branch : " + branchUuid);
        }

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

    @Override
    public void update() {

    }

    @Override
    public EstablishmentType getEstablishmentType() {
        return EstablishmentType.HeadQuarter;
    }

    @Override
    public List<IEstablishment> getSubEstablishment() {
        return branches.stream().map(branch -> (IEstablishment) branch).toList();
    }

    public void displayInfo(FullPlayer player) {
        MutableComponent message = MutableComponent.create(ComponentContents.EMPTY);

        //CityRank rank = WarOfSquirrels.instance.getConfig().getCityRankMap().get(cityUpgrade.getLevel().getCurrentLevel());

//        int size = WarOfSquirrels.instance.getChunkHandler().getSize(this);

        message.append("--==| " + displayName + " [" + members.size() + "] |==--\n");
        message.append("  Faction: " + cityHeadQuarter.getFaction().getDisplayName() + "\n");
        message.append("  HeadQuarter: " + cityHeadQuarter.getDisplayName() + "\n");
        message.append("  Guild master: " + owner.getDisplayName() + "\n");
//        message.append("  Assistant(s): " + Utils.getStringFromPlayerList(getAssistants()) + "\n");
//        message.append("  Resident(s): " + Utils.getStringFromPlayerList(getResidents()) + "\n");
//        message.append("  Recruit(s): " + Utils.getStringFromPlayerList(getRecruits()) + "\n");
        message.append("  Tag: " + tag + "\n");
        message.append("  Permissions:\n" + displayPermissions());

        message.withStyle(ChatFormatting.BLUE);
        player.sendMessage(message);
    }
}
