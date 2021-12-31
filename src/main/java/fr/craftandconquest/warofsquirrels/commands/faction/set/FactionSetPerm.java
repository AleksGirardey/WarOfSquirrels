package fr.craftandconquest.warofsquirrels.commands.faction.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.faction.FactionMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.faction.set.perm.FactionSetPermAlly;
import fr.craftandconquest.warofsquirrels.commands.faction.set.perm.FactionSetPermEnemy;
import fr.craftandconquest.warofsquirrels.commands.faction.set.perm.FactionSetPermFaction;
import fr.craftandconquest.warofsquirrels.commands.faction.set.perm.FactionSetPermOutside;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.text.MessageFormat;

public class FactionSetPerm extends FactionMayorOrAssistantCommandBuilder {
    private final FactionSetPermAlly factionSetPermAlly = new FactionSetPermAlly();
    private final FactionSetPermEnemy factionSetPermEnemy = new FactionSetPermEnemy();
    private final FactionSetPermFaction factionSetPermFaction = new FactionSetPermFaction();
    private final FactionSetPermOutside factionSetPermOutside = new FactionSetPermOutside();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("perm")
                .executes(this)
                .then(factionSetPermAlly.register())
                .then(factionSetPermEnemy.register())
                .then(factionSetPermFaction.register())
                .then(factionSetPermOutside.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String perm = "[build] [container] [switch] [farm] [interact]";
        player.sendMessage(ChatText.Success(MessageFormat.format(
                """
                        --==| faction set perm |==--
                         /faction set perm ally {0}
                         /faction set perm enemy {0}
                         /faction set perm faction {0}
                         /faction set perm outside {0}""", perm)));
        return 0;
    }
}
