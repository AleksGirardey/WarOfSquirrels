package fr.craftandconquest.warofsquirrels.commands.party;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class PartyRemoveCommand extends PartyCommandLeader implements IPlayerExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands
                .literal("remove")
                .then(getPlayerRegister()
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        Player target = getPlayer(context);
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);
        StringTextComponent message = new StringTextComponent("");

        if (target == null)
            message.appendText("Le joueur '" + getRawPlayer(context) + "' n'existe pas.");
        else if (!party.contains(player))
            message.appendText("Le joueur n'appartient pas à votre groupe.");
        else if (target == player)
            message.appendText("Vous ne pouvez pas vous exclure de votre propre groupe.");
        else
            return true;

        message.applyTextStyle(TextFormatting.RED);
        player.getPlayerEntity().sendMessage(message);
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        Player target = getPlayer(context);
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);

        StringTextComponent messageToParty =
                new StringTextComponent(target.getDisplayName() + " a été expulsé de votre groupe.");
        StringTextComponent messageToTarget =
                new StringTextComponent("Vous avez été expulsé de votre groupe");

        party.remove(target);
        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTarget(party, target);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(party, null, messageToParty, true);

        messageToParty.applyTextStyle(TextFormatting.GOLD);
        messageToTarget.applyTextStyle(TextFormatting.GOLD);
        target.getPlayerEntity().sendMessage(messageToTarget);
        return 0;
    }
}
