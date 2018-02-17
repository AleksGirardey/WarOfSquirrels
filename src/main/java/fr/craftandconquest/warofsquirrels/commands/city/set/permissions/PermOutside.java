package fr.craftandconquest.commands.city.set.permissions;

import fr.craftandconquest.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.args.CommandContext;

public class PermOutside extends CityCommandSetPerm{
    @Override
    protected void setPerm(DBPlayer player, CommandContext context) {
        this.perm = player.getCity().getPermOutside();
        this.name = "Outside";
    }
}
