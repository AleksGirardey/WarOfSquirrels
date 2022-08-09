package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.cubo.*;
import fr.craftandconquest.warofsquirrels.handler.CuboHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CuboCommandRegister extends CommandBuilder implements ICommandRegister {
    private final CuboList cuboList = new CuboList();
    private final CuboCreate cuboCreate = new CuboCreate();
    private final CuboDelete cuboDelete = new CuboDelete();
    private final CuboAdd cuboAdd = new CuboAdd();
    private final CuboRemove cuboRemove = new CuboRemove();
    private final CuboSet cuboSet = new CuboSet();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands
                .literal("cubo")
                .executes(this)
                .then(cuboList.register())
                .then(cuboCreate.register())
                .then(cuboDelete.register())
                .then(cuboAdd.register())
                .then(cuboRemove.register())
                .then(cuboSet.register()));
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() { return null; }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) { return true; }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        CuboHandler cuboHandler = WarOfSquirrels.instance.getCuboHandler();

        if (cuboHandler.playerExists(player))
            cuboHandler.deactivateCuboMode(player);
        else
            cuboHandler.activateCuboMode(player);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() { return null; }
}
