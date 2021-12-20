package fr.craftandconquest.warofsquirrels.commands.city.set.perm;

import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;

public class CitySetPermOutside extends CitySetPerm {
    @Override
    protected String getGroupTarget() {
        return "outside";
    }

    @Override
    protected PermissionRelation getPermissionRelation() {
        return PermissionRelation.OUTSIDER;
    }
}
