package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import fr.craftandconquest.warofsquirrels.commands.AdminCommandBuilder;
import net.minecraft.command.CommandSource;

public class CommandRegisterManager {
    private final CityCommandRegister cityCommandRegister = new CityCommandRegister();
    private final FactionComandRegister factionComandRegister = new FactionCommandRegister();
    private final PartyCommandRegister partyCommandRegister = new PartyCommandRegister();
    private final WarCommandRegister warCommandRegister = new WarCommandRegister();
    private final ShopCommandRegister shopCommandRegister = new ShopCommandRegister();
    private final AdminCommandRegister adminCommandRegister = new AdminCommandRegister();
    private final ChatCommandRegister chatCommandRegister = new CharCommandRegister();

    public void register(CommandDispatcher<CommandSource> dispatcher) {
        cityCommandRegister.register(dispatcher);
        warCommandRegister.register(dispatcher);
    }
}
