package fr.craftandconquest.warofsquirrels.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Vector3 {
    // Members
    @JsonProperty
    public float x;
    @JsonProperty
    public float y;
    @JsonProperty
    public float z;

    // Constructors
    public Vector3() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Compare two vectors
    public boolean equals(Vector3 other) {
        return (this.x == other.x && this.y == other.y && this.z == other.z);
    }

    public double distance(Vector3 b) {
        return distance(this, b);
    }

    public static double distance(Vector3 a, Vector3 b) {
        float v0 = b.x - a.x;
        float v1 = b.y - a.y;
        float v2 = b.z - a.z;
        return Math.sqrt(v0 * v0 + v1 * v1 + v2 * v2);
    }
}
