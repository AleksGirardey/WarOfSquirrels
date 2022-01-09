package fr.craftandconquest.warofsquirrels.commands.cubo;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.cubo.set.CuboSetCustomPerm;
import fr.craftandconquest.warofsquirrels.commands.cubo.set.CityCuboSetInPerm;
import fr.craftandconquest.warofsquirrels.commands.cubo.set.CityCuboSetOutPerm;
import fr.craftandconquest.warofsquirrels.commands.cubo.set.CityCuboSetOwner;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CuboSet extends CommandBuilder {
    private final CityCuboSetInPerm cityCuboSetInPerm = new CityCuboSetInPerm();
    private final CityCuboSetOutPerm cityCuboSetOutPerm = new CityCuboSetOutPerm();
    private final CuboSetCustomPerm cuboSetCustomPerm = new CuboSetCustomPerm();
    private final CityCuboSetOwner cityCuboSetOwner = new CityCuboSetOwner();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("set")
                .executes(this)
                .then(cityCuboSetInPerm.register())
                .then(cityCuboSetOutPerm.register())
                .then(cuboSetCustomPerm.register())
                .then(cityCuboSetOwner.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        player.sendMessage(ChatText.Success("""
                === Cubo set ===
                ... set owner : Set cubo's owner
                ... set inperm : Define permission for players on the list
                ... set outperm : Define permission for players outside the list
                ... set customperm : Define custom permission associated to a player or city or faction
                """));
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
