package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

public class CommandRegisterManager {
    private final WarCommandRegister warCommandRegister = new WarCommandRegister();

    public void register(CommandDispatcher<CommandSource> dispatcher) {
        warCommandRegister.register(dispatcher);
    }
}
