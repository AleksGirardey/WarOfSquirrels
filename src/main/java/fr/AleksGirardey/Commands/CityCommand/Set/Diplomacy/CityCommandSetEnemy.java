package fr.AleksGirardey.Commands.CityCommand.Set.Diplomacy;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class CityCommandSetEnemy extends CityCommandSetDiplomacy {
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

    protected void NewDiplomacy(int cityId1, int cityId2) {
        Annouce(cityId1, cityId2, "enemy");
        Core.getCityHandler().setDiplomacy(cityId1, cityId2, false);
    }
}
