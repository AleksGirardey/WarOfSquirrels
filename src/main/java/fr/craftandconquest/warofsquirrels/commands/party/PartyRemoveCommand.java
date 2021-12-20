package fr.craftandconquest.warofsquirrels.commands.party;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class PartyRemoveCommand extends PartyCommandLeader implements IPlayerExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("remove")
                .then(getPlayerRegister()
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getPlayer(context);
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);
        MutableComponent message;

        if (target == null)
            message = ChatText.Error("Le joueur '" + getRawPlayer(context) + "' n'existe pas.");
        else if (!party.contains(player))
            message = ChatText.Error("Le joueur n'appartient pas à votre groupe.");
        else if (target == player)
            message = ChatText.Error("Vous ne pouvez pas vous exclure de votre propre groupe.");
        else
            return true;

        player.getPlayerEntity().sendMessage(message, Util.NIL_UUID);
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getPlayer(context);
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);

        MutableComponent messageToParty =
                ChatText.Colored(target.getDisplayName() + " a été expulsé de votre groupe.", ChatFormatting.GOLD);
        MutableComponent messageToTarget =
                ChatText.Colored("Vous avez été expulsé de votre groupe", ChatFormatting.GOLD);

        party.remove(target);
        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTarget(party, target);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(party, null, messageToParty, true);

        target.getPlayerEntity().sendMessage(messageToTarget, Util.NIL_UUID);
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
