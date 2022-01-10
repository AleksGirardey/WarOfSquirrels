package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import fr.craftandconquest.warofsquirrels.commands.city.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CityCommandRegister implements ICommandRegister {
    private final CityHelp cityHelp = new CityHelp();
    private final CityInfo cityInfo = new CityInfo();
    private final CityCreate cityCreate = new CityCreate();
    private final CityDelete cityDelete = new CityDelete();
    private final CityClaim cityClaim = new CityClaim();
    private final CityUnClaim cityUnClaim = new CityUnClaim();
    private final CitySet citySet = new CitySet();
    private final CityAdd cityAdd = new CityAdd();
    private final CityRemove cityRemove = new CityRemove();
    private final CityLeave cityLeave = new CityLeave();
    private final CityList cityList = new CityList();
    private final CityTp cityTp = new CityTp();
    private final CityUpgradeCommand cityUpgradeCommand = new CityUpgradeCommand();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands
                .literal("city")
                .then(cityHelp.register())
                .then(cityInfo.register())
                .then(cityCreate.register())
                .then(cityDelete.register())
                .then(cityClaim.register())
                .then(cityUnClaim.register())
                .then(citySet.register())
                .then(cityAdd.register())
                .then(cityRemove.register())
                .then(cityLeave.register())
                .then(cityList.register())
                .then(cityUpgradeCommand.register())
                .then(cityTp.register())
                .executes(cityHelp)
        );
    }
}
