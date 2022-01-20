package fr.craftandconquest.warofsquirrels.object.faction.city;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.permission.*;
import fr.craftandconquest.warofsquirrels.object.upgrade.CityUpgrade;
import fr.craftandconquest.warofsquirrels.object.war.AttackTarget;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class City implements IPermission, IFortification, IChannelTarget, AttackTarget {
    @JsonProperty @Getter @Setter private UUID cityUuid;
    @JsonProperty @Setter public String displayName;
    @JsonProperty public String tag;
    @JsonProperty public UUID ownerUUID;
    @JsonProperty @Getter @Setter private UUID factionUuid;
    @JsonProperty @Getter @Setter private ChestLocation upgradeChestLocation;
    @JsonProperty @Getter @Setter private boolean hasAttackedToday = false;
    @JsonProperty @Getter @Setter private Map<PermissionRelation, Permission> defaultPermission;
    @JsonProperty @Getter @Setter private List<CustomPermission> customPermissionList = new ArrayList<>();
    @JsonProperty @Getter @Setter CityUpgrade cityUpgrade;

    @JsonIgnore @Getter private FullPlayer owner;
    @JsonIgnore private Faction faction = null;
    @JsonIgnore @Getter @Setter private Map<IPermission, Permission> customPermission = new HashMap<>();
    @JsonIgnore @Getter private final List<FullPlayer> citizens = new ArrayList<>();

    public List<FullPlayer> getAssistants() {
        return citizens.stream().filter(FullPlayer::getAssistant).collect(Collectors.toCollection(ArrayList::new));
    }

    @JsonIgnore
    public List<FullPlayer> getResidents() {
        return citizens.stream()
                .filter(FullPlayer::getResident)
                .filter(player -> !player.getAssistant())
                .filter(player -> !player.equals(owner))
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public List<FullPlayer> getRecruits() {
        return citizens.stream()
                .filter(player -> !player.equals(owner))
                .filter(player -> !player.getResident())
                .filter(player -> !player.getAssistant())
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public int getSize() {
        return citizens.size();
    }

    @JsonIgnore
    public boolean addCitizen(FullPlayer player) {
        if (!register(player)) return false;

        player.setAssistant(false);
        player.setResident(false);

        MutableComponent messageToTarget = new TextComponent("You joined " + displayName + ".")
                .withStyle(ChatFormatting.GREEN);
        player.sendMessage(messageToTarget);

        MutableComponent messageToCity = new TextComponent(player.getDisplayName() + " joined the city.")
                .withStyle(ChatFormatting.GREEN);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null, messageToCity, true);
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(this, player);
        return true;
    }

    public boolean removeCitizen(FullPlayer player, boolean isKicked) {
        if (!citizens.contains(player)) return false;

        citizens.remove(player);
        player.setCity(null);
        player.reset();

        player.sendMessage(ChatText.Error(isKicked ?
                "You got expelled of " + displayName + "." : "You left " + displayName));

        MutableComponent messageToCity = ChatText.Error(player.getDisplayName() + (isKicked ?
                " got expelled from the city." : " left the city."));

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null, messageToCity, true);
        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTarget(this, player);

        return true;
    }

    public void SetOwner(FullPlayer owner) {
        ownerUUID = owner != null ? owner.getUuid() : null;
        this.owner = owner;
    }

    public void SetFaction(Faction faction) {
        this.faction = faction;
        if (faction != null) {
            this.factionUuid = faction.getFactionUuid();
            faction.addCity(this);
        }
    }

    @Override
    public Faction getFaction() {
        return faction;
    }

    @JsonIgnore
    public int getMaxOutpost() {
        int hqLevel = cityUpgrade.getHeadQuarter().getCurrentLevel();

        if (hqLevel >= 3) return 5;
        else if (hqLevel == 2) return 2;

        return 0;
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

    @Override
    public City getRelatedCity() { return this; }

    @JsonIgnore
    public Chunk getHomeBlock() {
        return WarOfSquirrels.instance.getChunkHandler().getHomeBlock(this);
    }

    @JsonIgnore
    public int getCostReduction() {
        Territory target = WarOfSquirrels.instance.getTerritoryHandler().get(this);
        int base = cityUpgrade.getCostReduction();
        int territory = target != null ? (int) target.getBiome().ratioUpgradeReduction() : 0;

        return base + territory;
    }

    @JsonIgnore @Override
    public int getInfluenceGeneratedCloseNeighbour(boolean neutralOnly, boolean gotAttacked, boolean gotDefeated) {
        return 50;
    }

    @JsonIgnore @Override
    public int getSelfInfluenceGenerated(boolean gotAttacked, boolean gotDefeated) {
        int baseAmount = gotAttacked ? 0 : 100;
        int upgrade = gotDefeated ? 0 : WarOfSquirrels.instance.getBastionHandler().get(this).size() * 20;
        return baseAmount + upgrade;
    }

    @JsonIgnore @Override
    public int getInfluenceGeneratedDistantNeighbour(boolean gotAttacked, boolean gotDefeated) {
        int base = gotAttacked ? 0 : 0;
        int upgrade = gotDefeated ? 0 : getCityUpgrade().getInfluenceGeneratedDistantNeighbour();

        return base + upgrade;
    }

    @Override
    public int getInfluenceDamage(boolean gotAttacked, boolean gotDefeated) {
        return 0;
    }

    @JsonIgnore @Override
    public int getInfluenceRange() {
        return getCityUpgrade().getInfluenceRange();
    }

    @JsonIgnore @Override
    public int getInfluenceMax() {
        return 0;
    }

    @JsonIgnore
    public FortificationType getFortificationType() { return FortificationType.CITY; }

    @JsonIgnore
    public int getAmountBastions() {
        return WarOfSquirrels.instance.getBastionHandler().get(this).size();
    }

    @JsonIgnore
    public List<FullPlayer> getOnlinePlayers() {
        List<FullPlayer> onlinePlayers = new ArrayList<>();
        MinecraftServer server = WarOfSquirrels.server;

//        if (server.getPlayerList().getPlayer(owner.getUuid()) != null)
//            onlinePlayers.add(owner);

        for (FullPlayer player : citizens) {
            if (server.getPlayerList().getPlayer(player.getUuid()) != null)
                onlinePlayers.add(player);
        }

        return onlinePlayers;
    }

    @JsonIgnore
    public boolean canAttack() {
        if (cityUpgrade.getHeadQuarter().getCurrentLevel() >= 2) return true;

        return !hasAttackedToday;
    }

    public void displayInfo(FullPlayer player) {
        MutableComponent message = new TextComponent("");

        CityRank rank = WarOfSquirrels.instance.getConfig().getCityRankMap().get(cityUpgrade.getLevel().getCurrentLevel());

        int size = WarOfSquirrels.instance.getChunkHandler().getSize(this);

        message.append("--==| " + rank.getName() + " " + displayName + " [" + citizens.size() + "] |==--\n");
        message.append("  Faction: " + (faction == null ? "----" : faction.getDisplayName()) + "\n");
        message.append("  Mayor: " + owner.getDisplayName() + "\n");
        message.append("  Assistant(s): " + Utils.getStringFromPlayerList(getAssistants()) + "\n");
        message.append("  Resident(s): " + Utils.getStringFromPlayerList(getResidents()) + "\n");
        message.append("  Recruit(s): " + Utils.getStringFromPlayerList(getRecruits()) + "\n");
        message.append("  Tag: " + tag + "\n");
        message.append("  Chunks [" + size + "/" + rank.chunkMax + "]\n");
        message.append("  Outpost [" + WarOfSquirrels.instance.getChunkHandler().getOutpostSize(this) + "]\n");
        message.append(" -= Upgrades =-\n");
        message.append("  Level [" + cityUpgrade.getLevel().getCurrentLevel() + "/4]\n");
        message.append("  Housing [" + cityUpgrade.getHousing().getCurrentLevel() + "/4]\n");
        message.append("  Facility [" + cityUpgrade.getFacility().getCurrentLevel() + "/4]\n");
        message.append("  Head Quarter [" + cityUpgrade.getHeadQuarter().getCurrentLevel() + "/4]\n");
        message.append("  Palace [" + cityUpgrade.getPalace().getCurrentLevel() + "/4]\n");
        message.append("  Permissions:\n" + displayPermissions());

        message.withStyle(ChatFormatting.BLUE);
        player.sendMessage(message);
    }

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

    public void updateDependencies() {
        SetOwner(WarOfSquirrels.instance.getPlayerHandler().get(ownerUUID));
        SetFaction(WarOfSquirrels.instance.getFactionHandler().get(factionUuid));

        for (CustomPermission permission : customPermissionList) {
            IPermission target = permission.getTarget();

            if (target == null) continue;

            customPermission.put(target, permission.permission);
        }

        cityUpgrade.Populate(this);

        if (upgradeChestLocation != null)
            upgradeChestLocation.update();
    }

    public boolean register(FullPlayer player) {
        if (citizens.contains(player)) return false;

        citizens.add(player);
        player.setCity(this);

        return true;
    }

    public void Update() {
        hasAttackedToday = false;
        cityUpgrade.VerifyLevelUp();
    }

    @JsonIgnore @Override
    public Vector3 getSpawn() {
        return getHomeBlock().getRespawnPoint();
    }

    @JsonIgnore @Override
    public boolean isProtected() { return true; }

    @Override
    public int getMaxChunk() {
        return WarOfSquirrels.instance.getConfig().getCityRankMap().get(cityUpgrade.getLevel().getCurrentLevel()).chunkMax;
    }

    @Override
    public int getLinkedChunkSize() {
        return 0;
    }

    @Override
    public String getDisplayName() { return displayName; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        City city = (City) obj;

        return city.getUuid().equals(this.cityUuid);
    }
}
