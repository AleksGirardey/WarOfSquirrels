package fr.craftandconquest.commands.utils;

import fr.craftandconquest.commands.Commands;
import fr.craftandconquest.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class SetAdmin extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) { return true; }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        DBPlayer playerAdmin = context.<DBPlayer>getOne(Text.of("[joueur]")).orElse(player);

        playerAdmin.setAdminMode();

        player.sendMessage(Text.of(TextColors.GOLD, "--== Admin mode de " + playerAdmin + " [" + (playerAdmin.hasAdminMode() ? "ON" : "OFF") + "] ==--", TextColors.RESET));
        if (playerAdmin != player)
            playerAdmin.sendMessage(Text.of(TextColors.GOLD, "--== Admin mode [" + (playerAdmin.hasAdminMode() ? "ON" : "OFF") + "] ==--",TextColors.RESET));

        return CommandResult.success();
    }
}
