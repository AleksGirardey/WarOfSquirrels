package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IFactionExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class FactionInfoCommand extends CommandBuilder implements IFactionExtractor {
    private static final FactionInfoCommand withArgCmd = new FactionInfoCommand(true);

    public FactionInfoCommand() {
        this(false);
    }

    public FactionInfoCommand(boolean withArg) {
        this.withArg = withArg;
    }

    private final boolean withArg;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("info").executes(this)
                .then(getFactionRegister().executes(withArgCmd));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Faction faction;

        if (withArg) {
            faction = getFaction(context);
        } else {
            faction = player.getCity() != null ? player.getCity().getFaction() : null;
        }

        return faction != null;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Faction faction;

        if (withArg) {
            faction = getFaction(context);
        } else {
            faction = player.getCity().getFaction();
        }

        faction.displayInfo(player);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
