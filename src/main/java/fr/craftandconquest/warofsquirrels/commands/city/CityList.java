package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Pair;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;

public class CityList extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("list").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Map<Faction, List<Pair<City, Integer>>> factions = new HashMap<>();
        Map<Faction, Integer> factionMap = new LinkedHashMap<>();
        List<City> freeCityList = new ArrayList<>();
        List<City> cityList = WarOfSquirrels.instance.getCityHandler().getAll();

        for (City city : cityList) {
            Faction faction = city.getFaction();

            if (faction != null) {
                if (!factions.containsKey(faction)) {
                    factions.put(faction, new ArrayList<>());
                    factionMap.put(faction, 0);
                }
                factions.get(faction).add(new Pair<>(city, city.getOnlinePlayers().size()));
                factionMap.get
            } else {
                freeCityList.add(city);
            }
        }

        MutableComponent message = ChatText.Success("");
        factions.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(v -> v.ge)))
                .sorted((k, v) -> )


//        WarOfSquirrels.instance.getFactionHandler().getCitiesGroupedByFaction();
//         = WarOfSquirrels.instance.getCityHandler().getAll().stream().filter(c -> c.getFaction() == null).toList();


        message.append(ChatText.Success("=== Free cities [" + freeCityList.size() + "] ===\n"));

        for (int i = 0; i < freeCityList.size(); ++i) {
            message.append(freeCityList.get(i).displayName + "");
            if (i != freeCityList.size() - 1)
                message.append("");
        }
        player.sendMessage(message);
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("SHOULD NOT BE DISPLAYED");
    }
}
