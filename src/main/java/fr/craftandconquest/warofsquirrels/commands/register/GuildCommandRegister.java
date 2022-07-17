package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import fr.craftandconquest.warofsquirrels.commands.guild.GuildInfoCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class GuildCommandRegister implements ICommandRegister {
    private final GuildInfoCommand guildInfoCommand = new GuildInfoCommand();
    private final GuildCreateCommand guildCreateCommand = new GuildCreateCommand();
    private final GuildAddCommand guildAddCommand = new GuildAddCommand();
    private final GuildRemoveCommand guildRemoveCommand = new GuildRemoveCommand();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("guild")
                .then(guildInfoCommand.register())
                .then(guildCreateCommand.register())
                .then(guildAddCommand.register())
                .then(guildRemoveCommand.register())
        );
    }
}
