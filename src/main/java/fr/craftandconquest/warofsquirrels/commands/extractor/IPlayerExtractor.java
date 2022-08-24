package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public interface IPlayerExtractor extends IExtractor<FullPlayer> {

    List<PlayerExtractorType> getTargetSuggestionTypes();

    enum PlayerExtractorType {
        ALL,
        CITY_LESS,
        ALL_BUT_CITIZENS,
        CITIZENS,
        RESIDENT,
        RECRUIT,
        ASSISTANT,
        PARTY,
    }

    default SuggestionProvider<CommandSourceStack> getSuggestions() {
        return ((context, builder) -> {
            List<FullPlayer> players = new ArrayList<>();
            List<PlayerExtractorType> types = getTargetSuggestionTypes();
            Player playerEntity = context.getSource().getPlayerOrException();
            FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());

            if (player == null) return Suggestions.empty();

            City city = player.getCity();

            for (PlayerExtractorType type : types) {
                switch (type) {
                    case ALL -> players.addAll(WarOfSquirrels.instance.getPlayerHandler().getAll());
                    case CITY_LESS -> {
                        List<FullPlayer> all = new ArrayList<>(WarOfSquirrels.instance.getPlayerHandler().getAll());
                        all.removeIf(p -> p.getCity() != null);
                        players.addAll(all);
                    }
                    case ALL_BUT_CITIZENS -> {
                        if (city == null) continue;
                        players.addAll(WarOfSquirrels.instance.getPlayerHandler().getAll());
                        players.removeIf(p -> p.getCity().equals(city));
                    }
                    case CITIZENS -> {
                        if (city == null) continue;
                        players.addAll(city.getCitizens());
                    }
                    case RESIDENT -> {
                        if (city == null) continue;
                        players.addAll(city.getResidents());
                    }
                    case RECRUIT -> {
                        if (city == null) continue;
                        players.addAll(city.getRecruits());
                    }
                    case ASSISTANT -> {
                        if (city == null) continue;
                        players.addAll(city.getAssistants());
                    }
                    case PARTY -> {
                        if (!WarOfSquirrels.instance.getPartyHandler().Contains(player)) continue;
                        players.addAll(WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player).getPlayers());
                    }
                }
            }

            for (FullPlayer p : players) {
                builder.suggest(p.getDisplayName());
            }

            return builder.buildFuture();
        });
    }

    @Override
    default String getArgumentName()
    {
        return "PlayerName";
    }

    @Override
    default FullPlayer getArgument(CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getPlayerHandler().get(getRawArgument(context));
    }

}
