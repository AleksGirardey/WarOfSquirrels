package fr.craftandconquest.warofsquirrels.commands.faction.set.perm;

import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;

public class FactionSetPermAlly extends FactionSetPerm {
    @Override
    protected String getGroupTarget() {
        return "ally";
    }

    @Override
    protected PermissionRelation getPermissionRelation() {
        return PermissionRelation.ALLY;
    }
}
