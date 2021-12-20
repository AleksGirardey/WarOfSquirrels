package fr.craftandconquest.warofsquirrels.commands.city.set.perm;

import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;

public class CitySetPermResident extends CitySetPerm {
    @Override
    protected String getGroupTarget() {
        return "resident";
    }

    @Override
    protected PermissionRelation getPermissionRelation() {
        return PermissionRelation.RESIDENT;
    }
}
