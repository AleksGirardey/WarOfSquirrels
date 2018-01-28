package fr.AleksGirardey.Objects.CommandElements;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Attackable;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ElementAttackable extends CommandElement {
    public ElementAttackable(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        if (!(commandSource instanceof Player))
            throw commandArgs.createError(Text.of("Only a player can perform this command."));
        DBPlayer            player = Core.getPlayerHandler().get(((Player) commandSource).getPlayer().get());
        String              name = commandArgs.next();

        if (player.getCity() == null)
            throw commandArgs.createError(Text.of("Il est impossible d'attaquer seul ! Rejoignez une faction."));

        Map<String, Attackable> attackables = Core.getFactionHandler().getAttackables(player.getCity().getFaction());

        if (attackables.keySet().contains(name))
            return attackables.get(name);

        throw commandArgs.createError(Text.of("Aucune attaque n'est possible sur cet cible ou la cible n'éxiste pas."));
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        if (!(commandSource instanceof Player))
            return Collections.emptyList();
        DBPlayer            player = Core.getPlayerHandler().get((Player) commandSource);
        List<String>        list = new ArrayList<>();

        list.addAll(Core.getFactionHandler().getAttackables(player.getCity().getFaction()).keySet());
        return list;
    }
}
