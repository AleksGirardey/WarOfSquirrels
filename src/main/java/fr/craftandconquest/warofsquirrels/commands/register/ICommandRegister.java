package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public interface ICommandRegister {
    void register(CommandDispatcher<CommandSourceStack> dispatcher);
}
