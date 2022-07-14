package fr.craftandconquest.warofsquirrels.object.faction.guild;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.IUpdate;
import fr.craftandconquest.warofsquirrels.object.faction.Upgradable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class GuildBranch extends Upgradable implements IUpdate {
    @JsonProperty @Getter @Setter private List<UUID> shopList = new ArrayList<>();

    @Override
    public void update() { }
}
