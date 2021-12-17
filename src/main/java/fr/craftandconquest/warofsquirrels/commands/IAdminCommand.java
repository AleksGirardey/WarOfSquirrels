package fr.craftandconquest.warofsquirrels.commands;

import fr.craftandconquest.warofsquirrels.object.FullPlayer;

public interface IAdminCommand {
    default boolean IsAdmin(FullPlayer player) {
        return (player.isAdminMode()); //|| player.getPlayerEntity().hasPermissionLevel(2));
//                PermissionAPI.hasPermission(player.getPlayerEntity(), "minecraft.command.op"));
    }
}