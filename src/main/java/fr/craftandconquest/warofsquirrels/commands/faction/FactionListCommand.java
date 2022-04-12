package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class FactionListCommand extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("list").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        List<Faction> factionList = WarOfSquirrels.instance.getFactionHandler().getAll();

        MutableComponent message = ChatText.Colored("=== Factions [" + factionList.size() + "] ===\n", ChatFormatting.BLUE);

        for (Faction faction : factionList)
            message.append(faction.toString() + "\n");


        player.sendMessage(message);
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
