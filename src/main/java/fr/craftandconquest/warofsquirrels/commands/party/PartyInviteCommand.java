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
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

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
            message = ChatText.Error("Le joueur '" + getRawPlayer(context) + "' n'existe pas ou n'est pas connecté.");
        else if (WarOfSquirrels.instance.getPartyHandler().getFromPlayer(target) != null)
            message = ChatText.Error("Le joueur appartient déjà à un groupe.");
        else
            return true;

        player.getPlayerEntity().sendMessage(message, Util.NIL_UUID);
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getPlayer(context);
        Party party = WarOfSquirrels.instance.getPartyHandler().getPartyFromLeader(player);

        MutableComponent messageToParty =
                ChatText.Colored(target.getDisplayName() + " a été invité à rejoindre votre groupe.", ChatFormatting.GOLD);
        MutableComponent messageToTarget =
                ChatText.Colored(player.getDisplayName() + " vous a invité à rejoindre son groupe.", ChatFormatting.GOLD);

        WarOfSquirrels.instance.getInvitationHandler().CreateInvitation(new PartyInvitation(target, player, party));
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(party, null, messageToParty, true);
        target.getPlayerEntity().sendMessage(messageToTarget, Util.NIL_UUID);
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
