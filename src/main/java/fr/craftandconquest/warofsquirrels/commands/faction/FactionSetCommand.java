package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.faction.set.FactionSetAlly;
import fr.craftandconquest.warofsquirrels.commands.faction.set.FactionSetCapital;
import fr.craftandconquest.warofsquirrels.commands.faction.set.FactionSetEnemy;
import fr.craftandconquest.warofsquirrels.commands.faction.set.FactionSetNeutral;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class FactionSetCommand extends CommandBuilder {
    private static final FactionSetCapital factionSetCapital = new FactionSetCapital();
    private static final FactionSetAlly factionSetAlly = new FactionSetAlly();
    private static final FactionSetEnemy factionSetEnemy = new FactionSetEnemy();
    private static final FactionSetNeutral factionSetNeutral = new FactionSetNeutral();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("set")
                .executes(this)
                .then(factionSetCapital.register())
                .then(factionSetAlly.register())
                .then(factionSetEnemy.register())
                .then(factionSetNeutral.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        player.getPlayerEntity().sendMessage(ChatText.Colored("""
                --==| faction set help |==--
                /faction set ally [faction] <build> <container> <switch>
                /faction set enemy [faction] <build> <container> <switch>
                /faction set neutral [faction]
                """, ChatFormatting.GOLD), Util.NIL_UUID);
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
