package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.cubo.*;
import fr.craftandconquest.warofsquirrels.handler.CuboHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CityCubo extends CityMayorOrAssistantCommandBuilder {
    private final CuboList cityCuboList = new CuboList();
    //private final CityCuboLeave cityCuboLeave = new CityCuboLeave();
    private final CuboCreate cityCuboCreate = new CuboCreate();
    private final CuboDelete cityCuboDelete = new CuboDelete();
    private final CuboAdd cityCuboAdd = new CuboAdd();
    private final CuboRemove cityCuboRemove = new CuboRemove();
    private final CuboSet cityCuboSet = new CuboSet();

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
