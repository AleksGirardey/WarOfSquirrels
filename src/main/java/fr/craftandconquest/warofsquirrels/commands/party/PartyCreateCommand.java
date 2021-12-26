package fr.craftandconquest.warofsquirrels.commands.party;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class PartyCreateCommand extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("create").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player) == null) return true;

        player.sendMessage(ChatText.Error("Vous appartenez déjà à un groupe."));
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        WarOfSquirrels.instance.getPartyHandler().CreateParty(player);

        player.sendMessage(ChatText.Success("Vous appartenez désormais à un groupe."));
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You cannot perform this command");
    }
}
