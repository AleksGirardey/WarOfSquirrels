package fr.craftandconquest.warofsquirrels.commands.score;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.ScoreComparable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ScoreCommandPlayer extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("player").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        List<FullPlayer> players = WarOfSquirrels.instance.getPlayerHandler().getAll();
        SortedSet<ScoreComparable> scoreboard = new TreeSet<>();

        for (FullPlayer fullPlayer : players)
            scoreboard.add(new ScoreComparable(fullPlayer.getDisplayName(), fullPlayer.getScore()));

        player.sendMessage(ChatText.Colored("  -=| Scoreboard Players |=-  ", ChatFormatting.DARK_PURPLE));

        int index = 1;
        for (ScoreComparable score : scoreboard) {
            if (index > 10) break;
            player.sendMessage(ChatText.Colored("[" + index + "] " + score, ChatFormatting.LIGHT_PURPLE));
            ++index;
        }

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("Could not perform 'ScoreCommandPlayer' command.");
    }
}
