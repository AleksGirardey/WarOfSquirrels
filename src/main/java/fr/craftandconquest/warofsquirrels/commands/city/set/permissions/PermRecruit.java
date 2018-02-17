package fr.craftandconquest.warofsquirrels.commands.city.set.permissions;

import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.args.CommandContext;

public class PermRecruit extends CityCommandSetPerm {
    @Override
    protected void setPerm(DBPlayer player, CommandContext context) {
        this.perm = player.getCity().getPermRec();
        this.name = "Recrue";
    }
}
