package fr.craftandconquest.warofsquirrels.commands.party;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class PartyLeaveCommand extends PartyCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("leave").executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);

        return player != party.getLeader();
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);

        StringTextComponent messageToParty = new StringTextComponent(player.getDisplayName() + " a quitté le groupe.");
        StringTextComponent messageToPlayer = new StringTextComponent("Vous avez quitté votre groupe.");

        messageToParty.applyTextStyle(TextFormatting.GOLD);
        messageToPlayer.applyTextStyle(TextFormatting.GOLD);

        party.remove(player);
        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTarget(party, player);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(party, null, messageToParty, true);
        player.getPlayerEntity().sendMessage(messageToPlayer);
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return new StringTextComponent("Vous ne pouvez pas quitter un groupe dont vous êtes le chef.");
    }
}
