package fr.craftandconquest.warofsquirrels.commands.player;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;

public class PlayerInfoCommand extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("info").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        MutableComponent component = MutableComponent.create(ComponentContents.EMPTY);

        component.withStyle(ChatFormatting.GREEN);

        long currentTimestamp = System.currentTimeMillis();
        player.setPlayTimeInMillis(player.getPlayTimeInMillis() + (currentTimestamp - player.getConnectionTimestamp()));
        player.setConnectionTimestamp(currentTimestamp);

        component.append("--==| " + player.getDisplayName() + " |==--");
        component.append("\n  Play time: " + Utils.getPlayTime(player.getPlayTimeInMillis()));
        component.append("\n  Deaths: " + player.getDeathCount());
        component.append("\n  Score: " + player.getScore());
        component.append("\n -= PvP =-");
        component.append("\n    FFA kills:      " + player.getFreeForAllPlayerKillCount());
        component.append("\n    War kills:      " + player.getWarPlayerKillCount());
        component.append("\n    Target kills:  " + player.getWarTargetKillCount());
        component.append("\n -= PvE =-");
        component.append("\n    Monster kills: " + player.getMonsterKillCount());
        component.append("\n    Mobs kills:     " + player.getMobKillCount());

        player.sendMessage(component);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
