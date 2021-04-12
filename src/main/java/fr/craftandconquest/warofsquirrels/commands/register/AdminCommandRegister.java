package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminChunkInfoCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class AdminCommandRegister implements ICommandRegister {
    private final AdminChunkInfoCommand adminChunkInfoCommand = new AdminChunkInfoCommand();
    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("admin")
                .then(adminChunkInfoCommand.register()));
    }
}
