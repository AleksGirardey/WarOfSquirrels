package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import fr.craftandconquest.warofsquirrels.commands.party.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class PartyCommandRegister implements ICommandRegister {
    private final PartyHelpCommand partyHelpCommand = new PartyHelpCommand();
    private final PartyInfoCommand partyInfoCommand = new PartyInfoCommand();
    private final PartyCreateCommand partyCreateCommand = new PartyCreateCommand();
    private final PartyDeleteCommand partyDeleteCommand = new PartyDeleteCommand();
    private final PartyInviteCommand partyInviteCommand = new PartyInviteCommand();
    private final PartyRemoveCommand partyRemoveCommand = new PartyRemoveCommand();
    private final PartyLeaveCommand partyLeaveCommand = new PartyLeaveCommand();

    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("party")
                .executes(partyHelpCommand)
                .then(partyHelpCommand.register())
                .then(partyInfoCommand.register())
                .then(partyCreateCommand.register())
                .then(partyDeleteCommand.register())
                .then(partyInviteCommand.register())
                .then(partyRemoveCommand.register())
                .then(partyLeaveCommand.register()));
    }
}
