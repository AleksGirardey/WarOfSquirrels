package fr.craftandconquest.warofsquirrels.commands.faction;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Influence;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Territory;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class FactionClaim extends FactionCommandAssistant {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        Location<World> pos = player.getUser().getPlayer().get().getLocation();
        int x = pos.getBlockX() / Core.getConfig().getTerritorySize();
        int z = pos.getBlockZ() / Core.getConfig().getTerritorySize();
        Territory territory = Core.getTerritoryHandler().get(x, z, player.getUser().getPlayer().get().getWorld());
        Influence influence = Core.getInfluenceHandler().get(player.getCity().getFaction(), territory);

        return influence != null && influence.getInfluence() >= Core.getConfig().getTerritoryClaimLimit();
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        if (!player.getUser().getPlayer().isPresent()) return CommandResult.empty();
        Location<World> pos = player.getUser().getPlayer().get().getLocation();
        int x = pos.getBlockX() / Core.getConfig().getTerritorySize();
        int z = pos.getBlockZ() / Core.getConfig().getTerritorySize();
        Territory territory = Core.getTerritoryHandler().get(x, z, player.getUser().getPlayer().get().getWorld());
        territory.setFaction(player.getCity().getFaction());
        if (context.hasAny(Text.of("[name]")))
            territory.setName(context.<String>getOne(Text.of("[name]")).get());
        Core.sendText(Text.of(
                TextColors.GOLD, "La faction ", TextStyles.BOLD, TextStyles.ITALIC,
                player.getCity().getFaction().getDisplayName(), TextStyles.RESET,
                " a revendiqué l'appartenance du territoire ", TextStyles.BOLD, TextStyles.ITALIC,
                territory.getName(), TextStyles.RESET, TextColors.RESET));
        return CommandResult.success();
    }
}
