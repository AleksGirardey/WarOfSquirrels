package fr.craftandconquest.warofsquirrels.commands.party;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.invitation.PartyInvitation;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class PartyInviteCommand extends PartyCommandLeader implements IPlayerExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("invite").then(getPlayerRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getPlayer(context);
        MutableComponent message;

        if (target == null)
            message = ChatText.Error("Player '" + getRawPlayer(context).getDisplayName() + "' doesn't exist nor connected.");
        else if (WarOfSquirrels.instance.getPartyHandler().getFromPlayer(target) != null)
            message = ChatText.Error("Player already belongs to a party.");
        else
            return true;

        player.sendMessage(message);
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getPlayer(context);
        Party party = WarOfSquirrels.instance.getPartyHandler().getPartyFromLeader(player);

        MutableComponent messageToParty =
                ChatText.Colored(target.getDisplayName() + " has been invited to your party.", ChatFormatting.GOLD);
        MutableComponent messageToTarget =
                ChatText.Colored(player.getDisplayName() + " invited you to join his party.", ChatFormatting.GOLD);

        WarOfSquirrels.instance.getInvitationHandler().CreateInvitation(new PartyInvitation(target, player, party));
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(party, null, messageToParty, true);
        target.sendMessage(messageToTarget);
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }

    @Override
    public List<PlayerExtractorType> getTargetSuggestionTypes() {
        return List.of(PlayerExtractorType.ALL);
    }
}
