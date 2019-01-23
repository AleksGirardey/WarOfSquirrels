package fr.craftandconquest.warofsquirrels.commands.city.set.permissions;

import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.args.CommandContext;

public class PermResident extends CityCommandSetPerm {
    @Override
    protected void setPerm(DBPlayer player, CommandContext context) {
        this.perm = player.getCity().getPermRes();
        this.name = "Citoyen";
    }
}
