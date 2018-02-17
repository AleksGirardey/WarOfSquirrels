package fr.craftandconquest.warofsquirrels.commands.faction;

import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.args.CommandContext;

public abstract class FactionCommandMayor extends FactionCommand {

    protected abstract boolean SpecialCheck(DBPlayer player, CommandContext context);

    @Override
    protected boolean CanDoIt(DBPlayer player) {
        return super.CanDoIt(player) && (player.getCity().getFaction().getCapital().getOwner().equals(player));
    }
}
