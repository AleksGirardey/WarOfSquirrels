package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.MessageFormat;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class Diplomacy {
    @JsonProperty @Getter private UUID factionUuid;
    @JsonProperty @Getter private UUID targetUuid;
    @JsonProperty @Getter @Setter private boolean relation;
    @JsonProperty @Getter private UUID permissionUuid;

    @JsonIgnore @Getter private Faction faction;
    @JsonIgnore @Getter private Faction target;
    @JsonIgnore @Getter private Permission permission;

    public void SetFaction(Faction faction) {
        this.faction = faction;

        if (faction != null)
            factionUuid = faction.getFactionUuid();
    }

    public void SetTarget(Faction target) {
        this.target = target;

        if (target != null)
            targetUuid = target.getFactionUuid();
    }

    public void SetPermission(Permission permission) {
        this.permission = permission;

        if (permission != null)
            permissionUuid = permission.getUuid();
    }

    @Override
    public String toString() {
        return MessageFormat.format("[Diplomacy][Creation] diplomacy between '{0}' and '{1}' set to {2}",
                faction.getDisplayName(), target.getDisplayName(), relation ? "Allies" : "Enemies");
    }
}
