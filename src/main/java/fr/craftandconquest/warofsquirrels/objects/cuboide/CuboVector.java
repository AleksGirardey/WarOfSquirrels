package fr.craftandconquest.warofsquirrels.objects.cuboide;

import com.flowpowered.math.vector.Vector3i;
import fr.craftandconquest.warofsquirrels.objects.Core;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CuboVector {
    private Vector3i        A;
    private Vector3i        B;

    public CuboVector(Vector3i A, Vector3i B) {
        this.A = A;
        this.B = B;
        Core.getLogger().warn("Created CuboVector A[" + A.getX() + ";" + A.getY() + ";" + A.getZ() + "] B[" + B.getX() + ";" + B.getY() + ";" + B.getZ() + "]");
    }

    public CuboVector(ResultSet rs) throws SQLException {
        this(CuboVector.makeVector(
                rs.getInt("cubo_BposX"),
                rs.getInt("cubo_BposY"),
                rs.getInt("cubo_BposX")),
             CuboVector.makeVector(
                rs.getInt("cubo_AposX"),
                rs.getInt("cubo_AposY"),
                rs.getInt("cubo_AposX")));
    }

    private static Vector3i     makeVector(int x, int y, int z) {
        return new Vector3i(x, y, z);
    }

    public boolean      contains(Vector3i block) {
        int     x = block.getX(),
                y = block.getY(),
                z = block.getZ();

        boolean compareX = (A.getX() <= B.getX() ? (x >= A.getX() && x <= B.getX()) : (x >= B.getX() && x <= A.getX()));
        boolean compareY = (A.getY() <= B.getY() ? (y >= A.getY() && y <= B.getY()) : (y >= B.getY() && y <= A.getY()));
        boolean compareZ = (A.getZ() <= B.getZ() ? (z >= A.getZ() && z <= B.getZ()) : (z >= B.getZ() && z <= A.getZ()));

        return (compareX && compareY && compareZ);
    }

    public Vector3i getA() {
        return A;
    }

    public Vector3i getB() {
        return B;
    }

    @Override
    public String toString() {
        return "[" + A.getX() + ";" + A.getY() + ";" + A.getZ() + "][" + B.getX() + ";" + B.getY() + ";" + B.getZ() + "]";
    }
}
