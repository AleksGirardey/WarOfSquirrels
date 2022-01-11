package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import fr.craftandconquest.warofsquirrels.commands.war.*;
import net.minecraft.commands.CommandSourceStack;

import static net.minecraft.commands.Commands.literal;

public class WarCommandRegister implements ICommandRegister {
    private final WarHelp warHelpCommand = new WarHelp();
    private final WarAttack declareWarCommand = new WarAttack();
    private final WarInfo warInfoCommand = new WarInfo();
    private final WarJoin warJoinCommand = new WarJoin();
    private final WarLeave warLeaveCommand = new WarLeave();
    private final WarList warListCommand = new WarList();
    private final WarSetTarget warSetTargetCommand = new WarSetTarget();

    /**
     * Admin Commands
     **/
    private final ForceWin forceWinCommand = new ForceWin();
    private final WarPeaceTime warWorldAtPeaceCommand = new WarPeaceTime();

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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
                        .executes(warHelpCommand));
    }
}
