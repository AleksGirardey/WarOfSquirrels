package fr.craftandconquest.warofsquirrels.object.permission;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Permission {
    @Getter @Setter public UUID uuid;
    @Getter @Setter public String name;

    @Getter @Setter public boolean build;
    @Getter @Setter public boolean container;
    @Getter @Setter public boolean switches;
    @Getter @Setter public boolean farm;

    public Permission() {
        uuid = UUID.randomUUID();
    }

    public Permission(boolean build, boolean container, boolean switches, boolean farm) {
        super();
        this.build = build;
        this.container = container;
        this.switches = switches;
        this.farm = farm;
    }

    public Permission(String name, boolean build, boolean container, boolean switches, boolean farm) {
        super();
        this.name = name;
        this.build = build;
        this.container = container;
        this.switches = switches;
        this.farm = farm;
    }

    @Override
    public String       toString() {
        String          res = "";

        res += "[" + (this.build ? "B" : "-") + ";";
        res += (this.container ? "C" : "-") + ";";
        res += (this.switches ? "S" : "-") + ";";
        res += (this.farm ? "F" : "-") + "]";

        return res;
    }
}
