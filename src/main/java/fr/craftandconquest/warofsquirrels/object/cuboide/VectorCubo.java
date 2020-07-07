package fr.craftandconquest.warofsquirrels.object.cuboide;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import lombok.Getter;

public class VectorCubo {
    @JsonProperty("FirstPoint") @Getter private final Vector3        A;
    @JsonProperty("SecondPoint") @Getter private final Vector3        B;

    public VectorCubo(Vector3 A, Vector3 B) {
        this.A = A;
        this.B = B;
        WarOfSquirrels.LOGGER.warn("Created CuboVector A[" + A.x + ";" + A.y + ";" + A.z + "] B[" + B.x + ";" + B.y + ";" + B.z + "]");
    }

    public boolean      contains(Vector3 block) {
        float   x = block.x,
                y = block.y,
                z = block.z;

        boolean compareX = (A.x <= B.x ? (x >= A.x && x <= B.x) : (x >= B.x && x <= A.x));
        boolean compareY = (A.y <= B.y ? (y >= A.y && y <= B.y) : (y >= B.y && y <= A.y));
        boolean compareZ = (A.z <= B.z ? (z >= A.z && z <= B.z) : (z >= B.z && z <= A.z));

        return (compareX && compareY && compareZ);
    }

    @Override
    public String toString() {
        return "[" + A.x + ";" + A.y + ";" + A.z + "][" + B.x + ";" + B.y + ";" + B.z + "]";
    }
}
