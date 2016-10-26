package fr.AleksGirardey.Objects.Cuboide;

import com.flowpowered.math.vector.Vector3i;

public class Cubo {
    private int         id;
    private String      name;
    private Cubo        parent;
    private String      owner;
    private int         permissionId;
    private int         priority;
    private CuboVector  vector;

    public Cubo(int id, String name, Cubo parent, String owner, CuboVector vector) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.owner = owner;
        this.vector = vector;
    }

    public boolean          contains (Vector3i block) {
        return vector.contains(block);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cubo getParent() {
        return parent;
    }

    public void setParent(Cubo parent) {
        this.parent = parent;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public CuboVector getVector() {
        return vector;
    }

    public void setVector(CuboVector vector) {
        this.vector = vector;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}