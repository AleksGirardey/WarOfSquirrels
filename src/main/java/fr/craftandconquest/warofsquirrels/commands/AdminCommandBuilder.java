package fr.craftandconquest.warofsquirrels.commands;

import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.server.permission.PermissionAPI;

public abstract class AdminCommandBuilder extends CommandBuilder {
    @Override
    protected boolean CanDoIt(Player player) {
        return super.CanDoIt(player) && (player.isAdminMode() ||
                PermissionAPI.hasPermission(player.getPlayerEntity(), "minecraft.command.op"));
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return new StringTextComponent("You have not enough rights to perform this command");
    }
}
