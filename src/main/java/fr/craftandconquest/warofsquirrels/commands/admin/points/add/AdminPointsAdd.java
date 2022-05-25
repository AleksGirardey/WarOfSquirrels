package fr.craftandconquest.warofsquirrels.commands.admin.points.add;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminPointsAdd extends AdminCommandBuilder {
    private AdminPointsAddPlayer adminPointsAddPlayer = new AdminPointsAddPlayer();
    private AdminPointsAddCity adminPointsAddCity = new AdminPointsAddCity();
    private AdminPointsAddFaction adminPointsAddFaction = new AdminPointsAddFaction();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("add")
                .then(adminPointsAddPlayer.register())
                .then(adminPointsAddCity.register())
                .then(adminPointsAddFaction.register())
                ;
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return 0;
    }
}
