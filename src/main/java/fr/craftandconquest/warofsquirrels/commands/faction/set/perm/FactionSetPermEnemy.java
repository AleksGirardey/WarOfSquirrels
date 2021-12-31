package fr.craftandconquest.warofsquirrels.commands.faction.set.perm;

import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;

public class FactionSetPermEnemy extends FactionSetPerm {
    @Override
    protected String getGroupTarget() {
        return "enemy";
    }

    @Override
    protected PermissionRelation getPermissionRelation() {
        return PermissionRelation.ENEMY;
    }
}
