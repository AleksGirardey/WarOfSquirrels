package fr.craftandconquest.warofsquirrels.commands.party;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class PartyLeaveCommand extends PartyCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("leave").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);

        return player != party.getLeader();
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);

        MutableComponent messageToParty = ChatText.Colored(player.getDisplayName() + " a quitté le groupe.", ChatFormatting.GOLD);
        MutableComponent messageToPlayer = ChatText.Colored("Vous avez quitté votre groupe.", ChatFormatting.GOLD);

        party.remove(player);
        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTarget(party, player);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(party, null, messageToParty, true);
        player.getPlayerEntity().sendMessage(messageToPlayer, Util.NIL_UUID);
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("Vous ne pouvez pas quitter un groupe dont vous êtes le chef.");
    }
}
