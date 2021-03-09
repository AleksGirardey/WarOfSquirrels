package fr.craftandconquest.warofsquirrels.commands.city.set.perm;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;

public class CitySetPermOutside extends CityAssistantCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return null;
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        return 0;
    }
}
