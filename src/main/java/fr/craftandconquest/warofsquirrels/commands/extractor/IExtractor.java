package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;

public interface IExtractor<T> {
    String getRawArgument(CommandContext<CommandSource> context);
    T getArgument(Player player, CommandContext<CommandSource> context);
}
