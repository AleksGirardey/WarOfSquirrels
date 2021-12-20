package fr.craftandconquest.warofsquirrels.commands.city.set.perm;

import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;

public class CitySetPermRecruit extends CitySetPerm {
    @Override
    protected String getGroupTarget() {
        return "recruit";
    }

    @Override
    protected PermissionRelation getPermissionRelation() {
        return PermissionRelation.RECRUIT;
    }
}
