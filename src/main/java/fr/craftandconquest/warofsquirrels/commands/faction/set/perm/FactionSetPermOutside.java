package fr.craftandconquest.warofsquirrels.commands.faction.set.perm;

import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;

public class FactionSetPermOutside extends FactionSetPerm {
    @Override
    protected String getGroupTarget() {
        return "outside";
    }

    @Override
    protected PermissionRelation getPermissionRelation() {
        return PermissionRelation.OUTSIDER;
    }
}
