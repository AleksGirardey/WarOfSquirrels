package fr.craftandconquest.warofsquirrels.object.faction;

import java.util.UUID;

public interface IFortification {
    enum FortificationType {
        CITY,
        BASTION
    }

    UUID getUniqueId();

    Faction getFaction();

    default int getSelfInfluenceGenerated() {
        return 100;
    }

    default int getInfluenceGenerated() {
        return 100;
    }
}
