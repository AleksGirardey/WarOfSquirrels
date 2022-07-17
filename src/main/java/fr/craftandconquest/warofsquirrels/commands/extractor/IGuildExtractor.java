package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.faction.guild.Guild;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public interface IGuildExtractor extends IExtractor<Guild> {
    boolean isSuggestionFactionRestricted();

    @Override
    default String getArgumentName() { return "GuildName"; }

    @Override
    default Guild getArgument(CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getGuildHandler().get(getRawArgument(context));
    }

    @Override
    default SuggestionProvider<CommandSourceStack> getSuggestions() {
        return ((context, builder) -> {
            List<Guild> guilds;

            if (isSuggestionFactionRestricted()) {
                Player playerEntity = context.getSource().getPlayerOrException();
                FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());
                if (player.getCity() != null && player.getCity().getFaction() != null)
                    guilds = new ArrayList<>(WarOfSquirrels.instance.getGuildHandler().getAll(player.getCity().getFaction()));
                else
                    guilds = new ArrayList<>(WarOfSquirrels.instance.getGuildHandler().getAll());
            } else
                guilds = new ArrayList<>(WarOfSquirrels.instance.getGuildHandler().getAll());

            return suggestions(builder, guilds);
        });
    }
}
