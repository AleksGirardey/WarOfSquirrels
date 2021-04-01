package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public interface IFactionExtractor {
    String factionNameArgument = "[FactionName]";

    default String getRawFaction(CommandContext<CommandSource> context) {
        return context.getArgument(factionNameArgument, String.class);
    }

    default Faction getFaction(Player player, CommandContext<CommandSource> context) {
        return WarOfSquirrels.instance.getFactionHandler().get(getRawFaction(context));
    }

    default RequiredArgumentBuilder<CommandSource, String> getFactionRegister() {
        return Commands.argument(factionNameArgument, StringArgumentType.string());
    }
}
