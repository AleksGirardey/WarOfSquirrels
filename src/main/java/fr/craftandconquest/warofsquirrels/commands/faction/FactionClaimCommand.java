package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.extractor.ITerritoryExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.faction.city.CityRank;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class FactionClaimCommand extends FactionCommandAssistant implements ITerritoryExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("claim").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Territory territory = ExtractTerritory(player);
        Influence influence = WarOfSquirrels.instance.getInfluenceHandler().get(player.getCity().getFaction(), territory);

        if (influence == null || influence.getValue() < WarOfSquirrels.instance.getConfig().getBaseInfluenceRequired()) {
            player.sendMessage(ChatText.Error("You do not have enough influence to claim this territory"));
            return false;
        }

        CityRank rank = WarOfSquirrels.instance.getConfig().getCityRankMap().get(player.getCity().getCityUpgrade().getLevel().getCurrentLevel());

        if (WarOfSquirrels.instance.getBastionHandler().get(player.getCity()).size() >= rank.getBastionMax()) {
            player.sendMessage(ChatText.Error("You have reached your maximum number of bastion linked to this city"));
            return false;
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Territory territory = ExtractTerritory(player);

        territory.SetFaction(player.getCity().getFaction());

        int posX = territory.getPosX();
        int posZ = territory.getPosZ();
        int territorySize = WarOfSquirrels.instance.getConfig().getTerritorySize();

        MutableComponent message = ChatText.Colored("Faction '" + territory.getFaction().getDisplayName() + "'"
                + " claimed territory  '" + territory.getName() + "' at ["
                + posX + ";" + posZ + "](~" + posX * territorySize + ";" + "~" + posZ * territorySize + ")", ChatFormatting.GOLD);

        Player entity = player.getPlayerEntity();

        WarOfSquirrels.instance.getBastionHandler().Create(territory, player.getCity(), Utils.WorldToChunk(entity.getBlockX(), entity.getBlockZ()));
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
//        WarOfSquirrels.instance.getInfluenceHandler().ResetOthersInfluence(territory);

        WarOfSquirrels.instance.getBastionHandler().Save();
        WarOfSquirrels.instance.getTerritoryHandler().Save();
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("");
    }
}
