package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import fr.craftandconquest.warofsquirrels.commands.score.ScoreCommandFaction;
import fr.craftandconquest.warofsquirrels.commands.score.ScoreCommandPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ScoreCommandRegister implements ICommandRegister {
    private final ScoreCommandFaction scoreCommandFaction = new ScoreCommandFaction();
    private final ScoreCommandPlayer scoreCommandPlayer = new ScoreCommandPlayer();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("score")
                .then(scoreCommandFaction.register())
                .then(scoreCommandPlayer.register()));
    }
}
