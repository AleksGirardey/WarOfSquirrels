package fr.craftandconquest.warofsquirrels.commands.city.set.perm;

import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;

public class CitySetPermEnemy extends CitySetPerm {
    @Override
    protected String getGroupTarget() {
        return "enemy";
    }

    @Override
    protected PermissionRelation getPermissionRelation() {
        return PermissionRelation.ENEMY;
    }
}
