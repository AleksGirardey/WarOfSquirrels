package fr.craftandconquest.warofsquirrels.object.cuboide;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.PlayerHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.guild.Guild;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
public class Cubo {
    @JsonProperty
    @Getter
    @Setter
    private UUID uuid;
    @JsonProperty
    @Getter
    @Setter
    private String name;
    @JsonProperty
    @Getter
    @Setter
    private UUID cityUuid;

    @JsonProperty
    @Getter
    @Setter
    private UUID guildUuid;

    @JsonProperty
    @Getter
    @Setter
    private UUID parentUuid;
    @JsonProperty
    @Getter
    @Setter
    private UUID ownerUuid;
    @JsonProperty
    @Getter
    @Setter
    private Permission permissionIn;
    @JsonProperty
    @Getter
    @Setter
    private Permission permissionOut;
    //@JsonProperty private UUID permissionInUuid;
    //@JsonProperty private UUID permissionOutUuid;
    @JsonProperty
    private List<UUID> inListUuid = new ArrayList<>();
    //@JsonProperty private UUID loanUuid;
    @JsonProperty
    private Map<UUID, Permission> customInListUuid = new HashMap<>();
    @JsonProperty
    @Getter
    @Setter
    private int priority;
    @JsonProperty
    @Getter
    @Setter
    private VectorCubo vector;

    @JsonIgnore
    @Getter
    private City city;

    @JsonIgnore
    @Getter
    private Guild guild;
    @JsonIgnore
    @Getter
    private Cubo parent;
    @JsonIgnore
    @Getter
    private FullPlayer owner;
    @JsonIgnore
    @Getter
    private List<FullPlayer> inList = new ArrayList<>();
    @JsonIgnore
    private Map<FullPlayer, Permission> customInList = new HashMap<>();
//    @JsonIgnore @Getter private Loan loan;

    public void AddPlayerInList(FullPlayer player) {
        inList.add(player);
        inListUuid.add(player.getUuid());
    }

    public void RemovePlayerInList(FullPlayer target) {
        inList.remove(target);
        inListUuid.add(target.getUuid());
    }

    public void AddPlayerCustomPermission(FullPlayer player, Permission permission) {
        customInList.put(player, permission);
        customInListUuid.put(player.getUuid(), permission);
    }

    public void UpdateDependencies() {
        WarOfSquirrels wos = WarOfSquirrels.instance;
        PlayerHandler playerHandler = wos.getPlayerHandler();

        this.city = wos.getCityHandler().getCity(cityUuid);
        this.parent = wos.getCuboHandler().getCubo(parentUuid);
        this.owner = playerHandler.get(ownerUuid);
        this.inList = new ArrayList<>();

        if (inListUuid == null) inListUuid = new ArrayList<>();

        for (UUID uuid : inListUuid)
            inList.add(playerHandler.get(uuid));

        if (customInListUuid == null) customInListUuid = new HashMap<>();

        customInListUuid.forEach((k, v) -> customInList.put(playerHandler.get(k), v));
    }

    public void setCity(City city) {
        this.city = city;
        this.cityUuid = city != null ? city.getUuid() : null;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
        this.guildUuid = guild != null ? guild.getUuid() : null;
    }

    public void setParent(Cubo parent) {
        this.parent = parent;
        this.parentUuid = parent.getUuid();
    }

    public void setOwner(FullPlayer owner) {
        this.owner = owner;
        this.ownerUuid = owner.getUuid();
    }

    public void SpreadPermissionDelete(IPermission target) {
        if (target instanceof FullPlayer) {
            customInList.remove(target);
            customInListUuid.remove(target.getUuid());
        }
    }

    public Permission getCustomPermission(FullPlayer player) {
        return customInList.getOrDefault(player, null);
    }

    public MutableComponent display() {
        return ChatText.Success(name + " " + vector);
    }

    @Override
    public String toString() {
        return String.format("[Cubo][New] Cubo set as '%s' with parent '%s' owned by '%s'.",
                name, parent == null ? "NO_PARENT" : parent.name, owner.getDisplayName());
    }

}
