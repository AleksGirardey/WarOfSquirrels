package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public interface IFactionExtractor {
    String factionNameArgument = "[FactionName]";

    default String getRawFaction(CommandContext<CommandSourceStack> context) {
        return context.getArgument(factionNameArgument, String.class);
    }

    default Faction getFaction(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getFactionHandler().get(getRawFaction(context));
    }

    default RequiredArgumentBuilder<CommandSourceStack, String> getFactionRegister() {
        return Commands.argument(factionNameArgument, StringArgumentType.string());
    }
}
