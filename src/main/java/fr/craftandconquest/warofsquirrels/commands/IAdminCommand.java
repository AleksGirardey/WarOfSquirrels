package fr.craftandconquest.warofsquirrels.commands;

import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraftforge.server.permission.PermissionAPI;

public interface IAdminCommand {
    default boolean IsAdmin(Player player) {
        return (player.isAdminMode() ||
                PermissionAPI.hasPermission(player.getPlayerEntity(), "minecraft.command.op"));
    }
}
