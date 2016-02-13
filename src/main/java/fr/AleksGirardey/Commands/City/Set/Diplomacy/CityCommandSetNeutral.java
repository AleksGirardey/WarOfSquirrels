package fr.AleksGirardey.Commands.City.Set.Diplomacy;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class CityCommandSetNeutral extends CityCommandSetDiplomacy{
    @Override
    protected boolean CanDoIt(Player player) {
        return super.CanDoIt(player);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        return super.SpecialCheck(player, context);
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        return super.ExecCommand(player, context);
    }

    protected void NewDiplomacy(Player player, int cityId2) {
        int     cityId1 = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");
        Annouce(cityId1, cityId2, "neutral");
        Core.getCityHandler().setNeutral(cityId1, cityId2);
    }
}
