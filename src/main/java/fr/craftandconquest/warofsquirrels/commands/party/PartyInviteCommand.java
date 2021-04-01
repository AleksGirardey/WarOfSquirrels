package fr.craftandconquest.warofsquirrels.commands.party;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.invitation.PartyInvitation;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class PartyInviteCommand extends PartyCommandLeader implements IPlayerExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("invite").then(getPlayerRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        Player target = getPlayer(context);
        StringTextComponent message = new StringTextComponent("");

        if (target == null)
            message.appendText("Le joueur '" + getRawPlayer(context) + "' n'existe pas ou n'est pas connecté.");
        else if (WarOfSquirrels.instance.getPartyHandler().getFromPlayer(target) != null)
            message.appendText("Le joueur appartient déjà à un groupe.");
        else
            return true;

        message.applyTextStyle(TextFormatting.RED);
        player.getPlayerEntity().sendMessage(message);
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        Player target = getPlayer(context);
        Party party = WarOfSquirrels.instance.getPartyHandler().getPartyFromLeader(player);

        StringTextComponent messageToParty =
                new StringTextComponent(target.getDisplayName() + " a été invité à rejoindre votre groupe.");
        StringTextComponent messageToTarget =
                new StringTextComponent(player.getDisplayName() + " vous a invité à rejoindre son groupe.");

        messageToParty.applyTextStyle(TextFormatting.GOLD);
        messageToTarget.applyTextStyle(TextFormatting.GOLD);

        WarOfSquirrels.instance.getInvitationHandler().CreateInvitation(new PartyInvitation(target, player, party));
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(party, null, messageToParty, true);
        target.getPlayerEntity().sendMessage(messageToTarget);
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }
}
