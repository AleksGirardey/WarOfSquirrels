package fr.craftandconquest.warofsquirrels.object.permission;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Permission {
    @Getter
    @Setter
    public UUID uuid;
    @Getter
    @Setter
    public String name;

    @Getter
    @Setter
    public boolean build;
    @Getter
    @Setter
    public boolean container;
    @Getter
    @Setter
    public boolean switches;
    @Getter
    @Setter
    public boolean farm;
    @Getter
    @Setter
    public boolean interact;

    public Permission() {
        uuid = UUID.randomUUID();
    }

    public Permission(boolean build, boolean container, boolean switches, boolean farm, boolean interact) {
        super();
        this.build = build;
        this.container = container;
        this.switches = switches;
        this.farm = farm;
        this.interact = interact;
    }

    public Permission(String name, boolean build, boolean container, boolean switches, boolean farm, boolean interact) {
        super();
        this.name = name;
        this.build = build;
        this.container = container;
        this.switches = switches;
        this.farm = farm;
        this.interact = interact;
    }

    @Override
    public String toString() {
        String res = "";

        res += "[" + (this.build ? "B" : "-") + ";";
        res += (this.container ? "C" : "-") + ";";
        res += (this.switches ? "S" : "-") + ";";
        res += (this.farm ? "F" : "-") + ";";
        res += (this.interact ? "I" : "-") + "]";

        return res;
    }
}
