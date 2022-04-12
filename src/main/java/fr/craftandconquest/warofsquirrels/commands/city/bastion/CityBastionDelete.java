package fr.craftandconquest.warofsquirrels.commands.city.bastion;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityBastionCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CityBastionDelete extends CityBastionCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("delete").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Vector2 territoryPos = Utils.FromWorldToTerritory(player.getPlayerEntity().getBlockX(), player.getPlayerEntity().getBlockZ());
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().getFromTerritoryPos(territoryPos);

        WarOfSquirrels.instance.getBastionHandler().Delete((Bastion) territory.getFortification());

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(ChatText.Colored(
                "Faction " + player.getCity().getFaction().getDisplayName() + " has abandoned its control over territory '" + territory.getName() + "'",
                ChatFormatting.GOLD));

        return 0;
    }
}
