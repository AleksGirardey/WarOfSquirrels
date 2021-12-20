package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import fr.craftandconquest.warofsquirrels.commands.chat.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ChatCommandRegister implements ICommandRegister {
    private final ChatHelp chatHelp = new ChatHelp();
    private final ChatGeneral chatGeneral = new ChatGeneral();
    private final ChatCity chatCity = new ChatCity();
    private final ChatFaction chatFaction = new ChatFaction();
    private final ChatWar chatWar = new ChatWar();
    private final ChatParty chatParty = new ChatParty();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands
                .literal("chat")
                .then(chatHelp.register())
                .then(chatGeneral.register())
                .then(chatCity.register())
                .then(chatFaction.register())
                .then(chatWar.register())
                .then(chatParty.register())
                .executes(chatHelp));
    }
}
