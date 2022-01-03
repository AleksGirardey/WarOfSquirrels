package fr.craftandconquest.warofsquirrels.object.faction.city;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.core.BlockPos;

@AllArgsConstructor
@NoArgsConstructor
public class ChestLocation {
    @JsonProperty private Vector3 cornerOneVector;
    @JsonProperty private Vector3 cornerTwoVector;
    
    @JsonIgnore @Getter @Setter BlockPos cornerOne;
    @JsonIgnore @Getter @Setter BlockPos cornerTwo;

    public ChestLocation(Vector3 left, Vector3 right) {
        cornerOneVector = left;
        cornerTwoVector = right;
        update();
    }

    public void update() {
        if (cornerOneVector == null || cornerTwoVector == null) return;

        cornerOne = new BlockPos(cornerOneVector.x, cornerOneVector.y, cornerOneVector.z);
        cornerTwo = new BlockPos(cornerTwoVector.x, cornerTwoVector.y, cornerTwoVector.z);
    }
}
