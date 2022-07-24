package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.ITerritoryExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.channels.FactionChannel;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class FactionCreateCommand extends CityMayorCommandBuilder implements ITerritoryExtractor {
    private final String factionName = "[FactionName]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("create")
                .then(Commands.argument(factionName, StringArgumentType.string()).executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        // Une faction ne peut être créé que si :
        //  - La ville possède assez d'influence sur le territoire
        //  - La ville n'a pas déjà une faction

        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(player.getCity());
        Influence influence = WarOfSquirrels.instance.getInfluenceHandler().get(player.getCity(), territory);

        boolean hasFaction = player.getCity().getFaction() != null;
        boolean hasEnoughInfluence = influence != null && influence.getValue() >= WarOfSquirrels.instance.getConfig().getBaseInfluenceRequired();

        if (hasFaction) {
            player.sendMessage(ChatText.Error("You already got a faction."));
            return false;
        }

        if (!hasEnoughInfluence) {
            player.sendMessage(ChatText.Error("You do not have enough influence on your capital territory"));
            return false;
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String fName = context.getArgument(factionName, String.class);
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(player.getCity());
        Faction faction = WarOfSquirrels.instance.getFactionHandler().CreateFaction(fName, player.getCity());

        player.getCity().SetFaction(faction);

        if (!WarOfSquirrels.instance.getTerritoryHandler().Claim(territory.getPosX(), territory.getPosZ(), faction, player.getCity())) {
            WarOfSquirrels.instance.getFactionHandler().Delete(faction);
            return -1;
    }

        WarOfSquirrels.instance.getInfluenceHandler().SwitchInfluence(player.getCity(), faction, territory);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(ChatText.Colored(
                player.getDisplayName() + " has formed the nation '" + fName +
                        "' and set '" + player.getCity().getDisplayName() + "' as capital", ChatFormatting.GOLD));

        WarOfSquirrels.instance.getBroadCastHandler().AddTarget(faction, new FactionChannel(faction));
        for (FullPlayer p : player.getCity().getCitizens())
            WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(faction, p);

        WarOfSquirrels.instance.getInfluenceHandler().Save();
        WarOfSquirrels.instance.getCityHandler().Save();
        WarOfSquirrels.instance.getTerritoryHandler().Save();
        return 0;
    }

    @Override
    public boolean suggestionIsGlobalWarTarget() {
        return false;
    }

    @Override
    public boolean suggestionIsFactionWarTarget() {
        return false;
    }
}
