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
        return ChatText.Error("You need to physically the bastion to interact with it.");
    }

    @Override
    protected boolean CanDoIt(FullPlayer player) {
        WarOfSquirrels.instance.debugLog("CanDoIt 1");
        if (!player.getLastDimensionKey().equals(Level.OVERWORLD)) return false;
        WarOfSquirrels.instance.debugLog("CanDoIt 2");
        Vector2 territoryPos = Utils.FromWorldToTerritory(player.getPlayerEntity().getBlockX(), player.getPlayerEntity().getBlockZ());
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().getFromTerritoryPos(territoryPos);

        boolean isTerritoryNull = territory == null;
        boolean territoryHasFaction = !isTerritoryNull && territory.getFaction() != null;
        boolean territoryHasBastion = territoryHasFaction && territory.getFortification().getFortificationType().equals(IFortification.FortificationType.BASTION);

        WarOfSquirrels.instance.debugLog("CanDoIt 3 [" + territoryPos + "] " + isTerritoryNull + " / " + territoryHasFaction + " / " + !territoryHasBastion);

        if (territory == null
                || territory.getFaction() == null
                || !territory.getFortification().getFortificationType().equals(IFortification.FortificationType.BASTION))
            return false;

        WarOfSquirrels.instance.debugLog("CanDoIt 4");

        if (territory.getFaction().equals(player.getCity().getFaction())
                && territory.getFortification().getRelatedCity().equals(player.getCity())) {
            WarOfSquirrels.instance.debugLog("CanDoIt 5");
            return super.CanDoIt(player);
        }

        WarOfSquirrels.instance.debugLog("CanDoIt 6");
        return false;
    }
}
