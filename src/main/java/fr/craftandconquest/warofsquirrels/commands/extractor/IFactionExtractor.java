package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public interface IFactionExtractor extends IExtractor<Faction> {
    String factionNameArgument = "[FactionName]";

    default String getRawArgument(CommandContext<CommandSource> context) {
        return context.getArgument(factionNameArgument, String.class);
    }

    default Faction getArgument(Player player, CommandContext<CommandSource> context) {
        return WarOfSquirrels.instance.getFactionHandler().get(getRawArgument(context));
    }

    default RequiredArgumentBuilder<CommandSource, String> getArgumentRegister() {
        return Commands.argument(factionNameArgument, StringArgumentType.string());
    }
}
