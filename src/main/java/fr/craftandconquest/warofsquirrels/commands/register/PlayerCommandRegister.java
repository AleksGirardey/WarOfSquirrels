package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import fr.craftandconquest.warofsquirrels.commands.player.PlayerInfoCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class PlayerCommandRegister implements ICommandRegister {
    private final PlayerInfoCommand playerInfoCommand = new PlayerInfoCommand();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("player")
                .then(playerInfoCommand.register()));
    }
}
