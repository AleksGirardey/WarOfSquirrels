package fr.craftandconquest.warofsquirrels.commands.admin;

import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.network.chat.MutableComponent;

public abstract class AdminCommandBuilder extends CommandBuilder {
    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return super.CanDoIt(player) && (IsAdmin(player) || player.getPlayerEntity().hasPermissions(2));
//                PermissionAPI.hasPermission(player.getPlayerEntity(), "minecraft.command.op"));
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You have not enough rights to perform this command");
    }
}