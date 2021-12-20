package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.cubo.*;
import fr.craftandconquest.warofsquirrels.handler.CuboHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CityCubo extends CityMayorOrAssistantCommandBuilder {
    private final CityCuboList cityCuboList = new CityCuboList();
    //private final CityCuboLeave cityCuboLeave = new CityCuboLeave();
    private final CityCuboCreate cityCuboCreate = new CityCuboCreate();
    private final CityCuboDelete cityCuboDelete = new CityCuboDelete();
    private final CityCuboAdd cityCuboAdd = new CityCuboAdd();
    private final CityCuboRemove cityCuboRemove = new CityCuboRemove();
    private final CityCuboSet cityCuboSet = new CityCuboSet();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("cubo")
                .executes(this)
                .then(cityCuboList.register())
                //.then(cityCuboLeave.register())
                .then(cityCuboCreate.register())
                .then(cityCuboDelete.register())
                .then(cityCuboAdd.register())
                .then(cityCuboRemove.register())
                .then(cityCuboSet.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        CuboHandler cuboHandler = WarOfSquirrels.instance.getCuboHandler();

        if (cuboHandler.playerExists(player))
            cuboHandler.deactivateCuboMode(player);
        else
            cuboHandler.activateCuboMode(player);
        return 0;
    }
}
