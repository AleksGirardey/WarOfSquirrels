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
        if (!player.getLastDimensionKey().equals(Level.OVERWORLD)) return false;
        Vector2 territoryPos = Utils.WorldToTerritoryCoordinates(player.getPlayerEntity().getBlockX(), player.getPlayerEntity().getBlockZ());
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(territoryPos);

        if (territory == null
                || territory.getFaction() == null
                || !territory.getFortification().getFortificationType().equals(IFortification.FortificationType.BASTION))
            return false;

        if (territory.getFaction().equals(player.getCity().getFaction())
                && territory.getFortification().getRelatedCity().equals(player.getCity()))
            return super.CanDoIt(player);

        return false;
    }
}
