package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import fr.craftandconquest.warofsquirrels.commands.city.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CityCommandRegister implements ICommandRegister {
    private final CityHelp cityHelp = new CityHelp();
    private final CityInfo cityInfo = new CityInfo();
    private final CityCreate cityCreate = new CityCreate();
    private final CityDelete cityDelete = new CityDelete();
    private final CityClaim cityClaim = new CityClaim();
    private final CityUnClaim cityUnClaim = new CityUnClaim();

    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands
                .literal("city")
                .then(cityInfo.register())
                .then(cityCreate.register())
                .then(cityDelete.register())
                .then(cityClaim.register())
                .then(cityUnClaim.register())
                .executes(cityHelp)
        );
    }
}
