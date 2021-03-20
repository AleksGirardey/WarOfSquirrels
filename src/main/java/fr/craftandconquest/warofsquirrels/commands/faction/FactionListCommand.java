package fr.craftandconquest.warofsquirrels.commands.faction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class FactionListCommand extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("list").executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) { return true; }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        List<Faction> factionList = WarOfSquirrels.instance.getFactionHandler().getAll();

        StringTextComponent message = new StringTextComponent("=== Factions [" + factionList.size() + "] ===\n");

        for(Faction faction : factionList)
            message.appendText(faction.toString() + "\n");

        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() { return null; }
}
