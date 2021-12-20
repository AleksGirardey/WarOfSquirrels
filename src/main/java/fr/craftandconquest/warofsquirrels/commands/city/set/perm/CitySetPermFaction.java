package fr.craftandconquest.warofsquirrels.commands.city.set.perm;

import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;

public class CitySetPermFaction extends CitySetPerm {

    @Override
    protected String getGroupTarget() {
        return "faction";
    }

    @Override
    protected PermissionRelation getPermissionRelation() {
        return PermissionRelation.FACTION;
    }
}
