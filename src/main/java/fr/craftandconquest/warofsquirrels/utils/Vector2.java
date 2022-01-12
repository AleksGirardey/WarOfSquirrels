package fr.craftandconquest.warofsquirrels.utils;

public class Vector2 {
    // Members
    public float x;
    public float y;

    // Constructors
    public Vector2() {
        this.x = 0.0f;
        this.y = 0.0f;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Compare two vectors
    public boolean equals(Vector2 other) {
        return (this.x == other.x && this.y == other.y);
    }

    public double distance(Vector2 b) {
        return distance(this, b);
    }

    public static double distance(Vector2 a, Vector2 b) {
        float v0 = b.x - a.x;
        float v1 = b.y - a.y;
        return Math.sqrt(v0 * v0 + v1 * v1);
    }

    @Override
    public String toString() {
        return "[" + x + ";" + y + "]";
    }
}
