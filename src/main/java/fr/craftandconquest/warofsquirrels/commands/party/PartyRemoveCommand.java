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

import java.util.List;

public class PartyRemoveCommand extends PartyCommandLeader implements IPlayerExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("remove")
                .then(getArgumentRegister()
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getArgument(context);
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);
        MutableComponent message;

        if (target == null)
            message = ChatText.Error("Player '" + getRawArgument(context) + "' does not exist.");
        else if (!party.contains(player))
            message = ChatText.Error("Player do not belong to your party.");
        else if (target == player)
            message = ChatText.Error("you cannot exclude yourself from the party.");
        else
            return true;

        player.sendMessage(message);
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getArgument(context);
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);

        MutableComponent messageToParty =
                ChatText.Colored(target.getDisplayName() + " has been kicked from the party.", ChatFormatting.GOLD);
        MutableComponent messageToTarget =
                ChatText.Colored("You got kicked from your party.", ChatFormatting.GOLD);

        party.remove(target);
        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTarget(party, target);
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
        return List.of(PlayerExtractorType.PARTY);
    }
}
