package fr.craftandconquest.warofsquirrels.object.faction.city;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionTarget;
import fr.craftandconquest.warofsquirrels.object.war.AttackTarget;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class City implements IPermission, IFortification, IChannelTarget, AttackTarget {
    @JsonProperty @Getter @Setter private UUID cityUuid;
    @Getter @Setter public String displayName;
    public String tag;
    public UUID ownerUUID;

    @JsonIgnore @Getter private Faction faction;
    @JsonProperty @Getter @Setter private UUID factionUuid;
    @Getter private CityRank   rank;

    @Getter @Setter private Map<IPermission, Permission> customPermission;
    @Getter @Setter private Map<PermissionRelation, Permission> defaultPermission;

    private int         balance;

    @JsonIgnore @Getter private Player owner;
    @JsonIgnore @Getter private final List<Player> citizens = new ArrayList<>();

    public List<Player> getAssistants() {
        return citizens.stream().filter(Player::getAssistant).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Player> getResidents() {
        return citizens.stream()
                .filter(Player::getResident)
                .filter(player -> !player.getAssistant())
                .collect(Collectors.toList());
    }

    public List<Player> getRecruits() {
        return citizens.stream()
                .filter(player -> !player.getResident())
                .filter(player -> !player.getAssistant())
                .collect(Collectors.toList());
    }

    public boolean addCitizen(Player player) {
        if (citizens.contains(player)) return false;

        citizens.add(player);
        player.setCity(this);

        StringTextComponent messageToTarget = new StringTextComponent("Vous avez rejoint " + displayName + ".");
        messageToTarget.applyTextStyle(TextFormatting.GREEN);
        player.getPlayerEntity().sendMessage(messageToTarget);

        StringTextComponent messageToCity = new StringTextComponent(player.getDisplayName() + " à rejoint la ville.");
        messageToCity.applyTextStyle(TextFormatting.GREEN);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null, messageToCity, true);
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(this, player);
        return true;
    }

    public boolean removeCitizen(Player player, boolean isKicked) {
        if (!citizens.contains(player)) return false;

        citizens.remove(player);
        player.setCity(null);

        StringTextComponent messageToTarget = new StringTextComponent(isKicked ?
                "Vous avez été expulsé de " + displayName + "." : "Vous avez quitté " + displayName);
        messageToTarget.applyTextStyle(TextFormatting.RED);
        player.getPlayerEntity().sendMessage(messageToTarget);

        StringTextComponent messageToCity = new StringTextComponent(player.getDisplayName() + ( isKicked ?
                " a été expulsé de la ville." : " a quitté la ville."));
        messageToCity.applyTextStyle(TextFormatting.RED);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(this, null, messageToCity, true);

        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTarget(this, player);

        return true;
    }

    public void SetOwner(Player owner) {
        ownerUUID = owner != null ? owner.getUuid() : null;
        this.owner = owner;
    }

    public void SetRank(int rank) {
        this.rank = WarOfSquirrels.instance.getConfig().getCityRankMap().get(rank);
    }

    public void SetFaction(Faction faction) {
        this.faction = faction;
        this.factionUuid = faction.getFactionUuid();
    }

    @Override
    public PermissionTarget getPermissionTarget() {
        return PermissionTarget.CITY;
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

    public List<Player> getOnlinePlayers() {
        List<Player> onlinePlayers = new ArrayList<>();
        MinecraftServer server = WarOfSquirrels.server;

        if (server.getPlayerList().getPlayerByUUID(owner.getUuid()) != null)
            onlinePlayers.add(owner);

        for (Player player : citizens) {
            if (server.getPlayerList().getPlayerByUUID(player.getUuid()) != null)
                onlinePlayers.add(player);
        }

        return onlinePlayers;
    }

    public void displayInfo(Player player) {
        StringTextComponent message = new StringTextComponent("");

        WarOfSquirrels.instance.getChunkHandler().getSize(this);

        message.appendText("---===| " + rank.getName() + " " + displayName + " [" + citizens.size() + "] |===---\n");
        message.appendText("Faction: " + faction.getDisplayName() + "\n");
        message.appendText("Mayor: " + owner.getDisplayName() + "\n");
        message.appendText("Assistant(s): " + Utils.getStringFromPlayerList(getAssistants()) + "\n");
        message.appendText("Resident(s): " + Utils.getStringFromPlayerList(getCitizens()) + "\n");
        message.appendText("Recruit(s): " + Utils.getStringFromPlayerList(getRecruits()) + "\n");
        message.appendText("Tag: " + tag + "\n");
        message.appendText("Chunks [" +
                WarOfSquirrels.instance.getChunkHandler().getSize(this) + "/" +
                rank.chunkMax + "]\n");
        message.appendText("Outpost [" + WarOfSquirrels.instance.getChunkHandler().getOutpostSize(this) + "]\n");
        message.appendText("Permissions: " + defaultPermission.toString());

        message.applyTextStyle(TextFormatting.BLUE);
    }
}
