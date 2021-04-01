package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.faction.*;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class FactionCommandRegister implements ICommandRegister {
    private final FactionHelpCommand factionHelpCommand = new FactionHelpCommand();
    private final FactionInfoCommand factionInfoCommand = new FactionInfoCommand();
    private final FactionListCommand factionListCommand = new FactionListCommand();
    private final FactionClaimCommand factionClaimCommand = new FactionClaimCommand();
    private final FactionCreateCommand factionCreateCommand = new FactionCreateCommand();
    private final FactionDeleteCommand factionDeleteCommand = new FactionDeleteCommand();
    private final FactionSetCommand factionSetCommand = new FactionSetCommand();

    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("faction")
                .executes(factionHelpCommand)
                .then(factionInfoCommand.register())
                .then(factionListCommand.register())
                .then(factionClaimCommand.register())
                .then(factionCreateCommand.register())
                .then(factionDeleteCommand.register())
                .then(factionSetCommand.register())
        );
    }
}
