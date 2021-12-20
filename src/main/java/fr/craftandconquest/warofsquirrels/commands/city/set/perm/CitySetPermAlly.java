package fr.craftandconquest.warofsquirrels.commands.city.set.perm;

import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;

public class CitySetPermAlly extends CitySetPerm {
    @Override
    protected String getGroupTarget() {
        return "ally";
    }

    @Override
    protected PermissionRelation getPermissionRelation() {
        return PermissionRelation.ALLY;
    }
}
