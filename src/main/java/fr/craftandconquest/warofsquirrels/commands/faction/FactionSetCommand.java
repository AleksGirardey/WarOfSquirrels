package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.faction.set.FactionSetNeutral;
import fr.craftandconquest.warofsquirrels.commands.faction.set.FactionSetAlly;
import fr.craftandconquest.warofsquirrels.commands.faction.set.FactionSetCapital;
import fr.craftandconquest.warofsquirrels.commands.faction.set.FactionSetEnemy;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class FactionSetCommand extends CommandBuilder {
    private static final FactionSetCapital factionSetCapital = new FactionSetCapital();
    private static final FactionSetAlly factionSetAlly = new FactionSetAlly();
    private static final FactionSetEnemy factionSetEnemy = new FactionSetEnemy();
    private static final FactionSetNeutral factionSetNeutral = new FactionSetNeutral();

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("set")
                .executes(this)
                .then(factionSetCapital.register())
                .then(factionSetAlly.register())
                .then(factionSetEnemy.register())
                .then(factionSetNeutral.register());
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) { return true; }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        StringTextComponent msg = new StringTextComponent("--==| faction set help |==--\n" +
        "/faction set ally [faction] <build> <container> <switch>\n" +
                "/faction set enemy [faction] <build> <container> <switch>\n" +
                "/faction set neutral [faction]\n");
        msg.applyTextStyle(TextFormatting.GOLD);

        player.getPlayerEntity().sendMessage(msg);
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() { return null; }
}
