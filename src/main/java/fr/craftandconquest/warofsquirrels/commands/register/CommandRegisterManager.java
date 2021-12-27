package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import fr.craftandconquest.warofsquirrels.commands.AcceptCommand;
import fr.craftandconquest.warofsquirrels.commands.RefuseCommand;
import net.minecraft.commands.CommandSourceStack;

public class CommandRegisterManager {
    private final CityCommandRegister cityCommandRegister = new CityCommandRegister();
    private final FactionCommandRegister factionCommandRegister = new FactionCommandRegister();
    private final PartyCommandRegister partyCommandRegister = new PartyCommandRegister();
    private final WarCommandRegister warCommandRegister = new WarCommandRegister();
    private final AdminCommandRegister adminCommandRegister = new AdminCommandRegister();
    private final ChatCommandRegister chatCommandRegister = new ChatCommandRegister();

    private final AcceptCommand acceptCommand = new AcceptCommand();
    private final RefuseCommand refuseCommand = new RefuseCommand();

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        cityCommandRegister.register(dispatcher);
        warCommandRegister.register(dispatcher);
        factionCommandRegister.register(dispatcher);
        partyCommandRegister.register(dispatcher);
        adminCommandRegister.register(dispatcher);
        chatCommandRegister.register(dispatcher);

        dispatcher.register(acceptCommand.register());
        dispatcher.register(refuseCommand.register());
    }
}