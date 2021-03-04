package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import fr.craftandconquest.warofsquirrels.commands.war.*;
import net.minecraft.command.CommandSource;

import static net.minecraft.command.Commands.literal;

public class WarCommandRegister {
    private final WarHelp warHelpCommand = new WarHelp();
    private final DeclareWar declareWarCommand = new DeclareWar();
    private final WarInfo warInfoCommand = new WarInfo();
    private final WarJoin warJoinCommand = new WarJoin();
    private final WarLeave warLeaveCommand = new WarLeave();
    private final WarList warListCommand = new WarList();
    private final WarSetTarget warSetTargetCommand = new WarSetTarget();
    
    /** Admin Commands **/
    private final ForceWin forceWinCommand = new ForceWin();

    public void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                literal("war")
                        .then(declareWarCommand.register())
                        .then(forceWinCommand.register())
                        .then(warInfoCommand.register())
                        .then(warJoinCommand.register())
                        .then(warLeaveCommand.register())
                        .then(warListCommand.register())
                        .then(warWorldAtPeaceCommand.register())
                        .then(warSetTargetCommand.register())
                        .executes(warHelpCommand)


        );
    }
}
