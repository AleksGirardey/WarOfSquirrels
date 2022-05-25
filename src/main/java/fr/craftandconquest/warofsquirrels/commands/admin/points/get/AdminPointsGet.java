package fr.craftandconquest.warofsquirrels.commands.admin.points.get;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminPointsGet extends AdminCommandBuilder {
    private final AdminPointsGetPlayer adminPointsGetPlayer = new AdminPointsGetPlayer();
    private final AdminPointsGetCity adminPointsGetCity = new AdminPointsGetCity();
    private final AdminPointsGetFaction adminPointsGetFaction = new AdminPointsGetFaction();
    
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("get")
                .then(adminPointsGetPlayer.register())
                .then(adminPointsGetCity.register())
                .then(adminPointsGetFaction.register());
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
