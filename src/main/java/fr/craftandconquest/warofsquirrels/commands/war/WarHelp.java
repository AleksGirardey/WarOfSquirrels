package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class WarHelp extends CommandBuilder {
    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        PlayerEntity entity = player.getPlayerEntity();

        entity.sendMessage(new StringTextComponent("---=== /war ===---"));
        entity.sendMessage(new StringTextComponent("- war attack"));
        entity.sendMessage(new StringTextComponent("- war info"));
        entity.sendMessage(new StringTextComponent("- war join"));
        entity.sendMessage(new StringTextComponent("- war leave"));
        entity.sendMessage(new StringTextComponent("- war list"));
        entity.sendMessage(new StringTextComponent("- war target"));

        return 0;
    }

    /** No Implementations needed **/

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return null;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return true;
    }
}
