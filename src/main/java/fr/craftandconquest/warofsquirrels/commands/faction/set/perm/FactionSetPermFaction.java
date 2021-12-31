package fr.craftandconquest.warofsquirrels.commands.faction.set.perm;

import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;

public class FactionSetPermFaction extends FactionSetPerm {
    @Override
    protected String getGroupTarget() {
        return "faction";
    }

    @Override
    protected PermissionRelation getPermissionRelation() {
        return PermissionRelation.FACTION;
    }
}
