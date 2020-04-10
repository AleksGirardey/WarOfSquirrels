package fr.craftandconquest.warofsquirrels.objects.commandelements;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Faction;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ElementAlly extends CommandElement{
    public ElementAlly(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        if (!(commandSource instanceof Player))
            throw commandArgs.createError(Text.of("Only a player can perform this command."));

        DBPlayer    player = Core.getPlayerHandler().get((Player) commandSource);

        if (player.getCity() != null) {
            Faction         faction = player.getCity().getFaction();
            List<Faction>   allies = Core.getDiplomacyHandler().getAllies(faction);
            Faction         f = Core.getFactionHandler().get(commandArgs.next());

            if (f != null && allies.contains(f))
                return f;
        }
        throw commandArgs.createError(Text.of(" is not a valid faction"));
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        if (!(commandSource instanceof Player))
            return Collections.emptyList();
        DBPlayer    player = Core.getPlayerHandler().get((Player) commandSource);

        if (player.getCity() != null) {
            Faction     faction = player.getCity().getFaction();

            return Core.getFactionHandler().getAlliesName(faction);
        }
        return Collections.emptyList();
    }
}
