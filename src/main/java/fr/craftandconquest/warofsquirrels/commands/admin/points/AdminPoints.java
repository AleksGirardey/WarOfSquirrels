package fr.craftandconquest.warofsquirrels.commands.admin.points;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.admin.points.add.AdminPointsAdd;
import fr.craftandconquest.warofsquirrels.commands.admin.points.get.AdminPointsGet;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminPoints extends AdminCommandBuilder {
    private final AdminPointsAdd adminPointsAdd = new AdminPointsAdd();
    private final AdminPointsGet adminPointsGet = new AdminPointsGet();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("points")
                .then(adminPointsAdd.register())
                .then(adminPointsGet.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return 0;
    }
}
