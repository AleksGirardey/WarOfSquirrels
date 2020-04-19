package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.craftandconquest.warofsquirrels.object.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class City {
    public int cityId;
    public String displayName;
    public String tag;
    public UUID ownerUUID;
    private int   rank;
/**
    private Faction     faction;
    private Permission  permRec;
    private Permission  permRes;
    private Permission  permAllies;
    private Permission  permOutside;
    private Permission  permFaction;*/
    private int         balance;

    @JsonIgnore @Getter private Player owner;
    @JsonIgnore @Getter private List<Player> citizens;

    public boolean addCitizen(Player player) {
        if (citizens.contains(player)) return false;

        citizens.add(player);
        return true;
    }

    public void SetOwner(Player owner) {
        ownerUUID = owner != null ? owner.getUUID() : null;
        this.owner = owner;
    }

    public List<String> getCitizensAsList() {
        List<String> res = new ArrayList<>();

        for (Player player : citizens) {
            res.add(player.getDisplayName());
        }
        return res;
    }
}
