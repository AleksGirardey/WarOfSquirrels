package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.war.War;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

public interface IWarExtractor {
    String warNameArgument = "WarName";

    default String getRawArgument(CommandContext<CommandSourceStack> context) {
        return context.getArgument(warNameArgument, String.class);
    }

    default War getArgument(CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getWarHandler().getWar(getRawArgument(context));
    }

    default RequiredArgumentBuilder<CommandSourceStack, String> getArgumentRegister() {
        return Commands.argument(warNameArgument, StringArgumentType.string()).suggests(getSuggestions());
    }

    default SuggestionProvider<CommandSourceStack> getSuggestions() {
        return ((context, builder) -> {
            List<War> wars = WarOfSquirrels.instance.getWarHandler().getAll();

            for (War war : wars) {
                builder.suggest(war.getCityAttacker().getDisplayName());
                builder.suggest(war.getCityDefender().getDisplayName());
            }

            return builder.buildFuture();
        });
    }
}
