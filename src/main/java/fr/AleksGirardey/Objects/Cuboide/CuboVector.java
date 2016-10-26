package fr.AleksGirardey.Objects.Cuboide;

import com.flowpowered.math.vector.Vector3i;

import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;

public class CuboVector {
    private Vector3i        one;
    private Vector3i        two;
    private Vector3i        three;
    private Vector3i        four;
    private Vector3i        five;
    private Vector3i        six;
    private Vector3i        seven;
    private Vector3i        eight;

    public CuboVector(Vector3i A, Vector3i B) {
        if (A.getX() <= B.getX()
                && A.getY() <= B.getY()
                && A.getZ() <= B.getZ()) {
            one = A;
            eight = B;
        } else {
            one = B;
            eight = A;
        }

        two = new Vector3i(A.getX(), B.getY(), A.getZ());
        three = new Vector3i(B.getX(), A.getY(), A.getZ());
        four = new Vector3i(B.getX(), B.getZ(), A.getZ());
        five = new Vector3i(A.getX(), A.getY(), B.getZ());
        six = new Vector3i(A.getX(), B.getY(), B.getZ());
        seven = new Vector3i(B.getY(), A.getY(), B.getZ());
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
                y = block.getX(),
                z = block.getZ();

        return (x >= one.getX() && x <= three.getX()
                && y >= one.getY() && y <= two.getY()
                && z >= one.getZ() && z <= eight.getZ());
    }

    public Vector3i getOne() {
        return one;
    }

    public Vector3i getTwo() {
        return two;
    }

    public Vector3i getThree() {
        return three;
    }

    public Vector3i getFour() {
        return four;
    }

    public Vector3i getFive() {
        return five;
    }

    public Vector3i getSix() {
        return six;
    }

    public Vector3i getSeven() {
        return seven;
    }

    public Vector3i getEight() {
        return eight;
    }
}
