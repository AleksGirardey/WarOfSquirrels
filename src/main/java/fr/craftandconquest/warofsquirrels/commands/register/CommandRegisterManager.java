package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import fr.craftandconquest.warofsquirrels.commands.AcceptCommand;
import fr.craftandconquest.warofsquirrels.commands.ListCommand;
import fr.craftandconquest.warofsquirrels.commands.PouetCommand;
import fr.craftandconquest.warofsquirrels.commands.RefuseCommand;
import net.minecraft.commands.CommandSourceStack;

public class CommandRegisterManager {
    private final CityCommandRegister cityCommandRegister = new CityCommandRegister();
    private final FactionCommandRegister factionCommandRegister = new FactionCommandRegister();
    private final PartyCommandRegister partyCommandRegister = new PartyCommandRegister();
    private final WarCommandRegister warCommandRegister = new WarCommandRegister();
    private final AdminCommandRegister adminCommandRegister = new AdminCommandRegister();
    private final ChatCommandRegister chatCommandRegister = new ChatCommandRegister();
    private final CuboCommandRegister cuboCommandRegister = new CuboCommandRegister();

    private final AcceptCommand acceptCommand = new AcceptCommand();
    private final RefuseCommand refuseCommand = new RefuseCommand();
    private final ListCommand listCommand = new ListCommand();
    private final PouetCommand pouetCommand = new PouetCommand();

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        cityCommandRegister.register(dispatcher);
        warCommandRegister.register(dispatcher);
        factionCommandRegister.register(dispatcher);
        partyCommandRegister.register(dispatcher);
        adminCommandRegister.register(dispatcher);
        chatCommandRegister.register(dispatcher);
        cuboCommandRegister.register(dispatcher);

        dispatcher.register(acceptCommand.register());
        dispatcher.register(refuseCommand.register());
        dispatcher.register(listCommand.register());
        dispatcher.register(pouetCommand.register());
    }
}