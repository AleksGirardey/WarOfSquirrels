package fr.craftandconquest.warofsquirrels.commands.party;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class PartyCreateCommand extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("create").executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        if (WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player) == null) return true;

        StringTextComponent message = new StringTextComponent("Vous appartenez déjà à un groupe.");
        message.applyTextStyle(TextFormatting.RED);

        player.getPlayerEntity().sendMessage(message);
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        WarOfSquirrels.instance.getPartyHandler().CreateParty(player);
        StringTextComponent message = new StringTextComponent("Vous appartenez désormais à un groupe.");
        message.applyTextStyle(TextFormatting.GOLD);

        player.getPlayerEntity().sendMessage(message);
        return 0;
    }
}
