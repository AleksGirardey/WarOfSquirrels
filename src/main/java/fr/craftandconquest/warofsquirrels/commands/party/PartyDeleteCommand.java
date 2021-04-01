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

public class PartyDeleteCommand extends PartyCommandLeader {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("delete").executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        StringTextComponent message = new StringTextComponent("Votre groupe a été dissout.");
        message.applyTextStyle(TextFormatting.GOLD);
        Party party = WarOfSquirrels.instance.getPartyHandler().getPartyFromLeader(player);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(party, null, message, true);
        WarOfSquirrels.instance.getPartyHandler().RemoveParty(party);
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return new StringTextComponent("You can't perform this command").applyTextStyle(TextFormatting.RED);
    }
}
