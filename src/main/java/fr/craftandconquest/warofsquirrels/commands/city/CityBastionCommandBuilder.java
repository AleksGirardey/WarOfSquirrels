package fr.craftandconquest.warofsquirrels.commands.city;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;

public abstract class CityBastionCommandBuilder extends CityMayorOrAssistantCommandBuilder {
    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You need to be physically in the bastion to interact with it.");
    }

    @Override
    protected boolean CanDoIt(FullPlayer player) {
        if (!super.CanDoIt(player)) return false;

        if (!player.getLastDimensionKey().equals(Level.OVERWORLD)) return false;

        Vector2 territoryPos = Utils.FromWorldToTerritory(player.getPlayerEntity().getBlockX(), player.getPlayerEntity().getBlockZ());
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().getFromTerritoryPos(territoryPos);

        boolean isTerritoryNull = territory == null;
        boolean territoryHasFaction = !isTerritoryNull && territory.getFaction() != null;
        boolean territorySameFaction = territoryHasFaction && territory.getFaction().equals(player.getCity().getFaction());
        boolean territoryHasBastion = territoryHasFaction && territory.getFortification().getFortificationType().equals(IFortification.FortificationType.BASTION);
        boolean playerIsFactionLeader = player.getCity().getFaction().getCapital().getOwner().equals(player);

        if (isTerritoryNull) {
            player.sendMessage(ChatText.Error("You are not on a claimed territory"));
            return false;
        }

        if (!territorySameFaction) {
            player.sendMessage(ChatText.Error("Territory does not belong to your faction"));
            return false;
        }

        if (!territoryHasBastion) {
            player.sendMessage(ChatText.Error("Territory has no bastion to interact with"));
            return false;
        }

        if (!playerIsFactionLeader && !territory.getFortification().getRelatedCity().equals(player.getCity())) {
            player.sendMessage(ChatText.Error("Your rank does not allow you to interact with this bastion"));
            return false;
        }

        return true;
    }
}
