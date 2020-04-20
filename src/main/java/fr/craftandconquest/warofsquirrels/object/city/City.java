package fr.craftandconquest.warofsquirrels.object.city;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.craftandconquest.warofsquirrels.object.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class City {
    public int cityId;
    public String displayName;
    public String tag;
    public UUID ownerUUID;
    private CityRank   rank;
/**
    private Faction     faction;
    private Permission  permRec;
    private Permission  permRes;
    private Permission  permAllies;
    private Permission  permOutside;
    private Permission  permFaction;*/
    private int         balance;

    @JsonIgnore @Getter private Player owner;
    @JsonIgnore @Getter private List<Player> citizens = new ArrayList<>();

    public boolean addCitizen(Player player) {
        if (citizens.contains(player)) return false;

        citizens.add(player);
        return true;
    }

    public boolean removeCitizen(Player player) {
        if (!citizens.contains(player)) return false;

        return citizens.remove(player);
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
