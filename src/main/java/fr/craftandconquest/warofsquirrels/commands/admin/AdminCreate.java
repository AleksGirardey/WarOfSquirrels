package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.admin.create.AdminCreateFakePlayer;
import fr.craftandconquest.warofsquirrels.commands.admin.create.AdminCreateProtection;
import fr.craftandconquest.warofsquirrels.commands.admin.create.AdminCreateTeleporter;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminCreate extends AdminCommandBuilder {
    private final AdminCreateFakePlayer adminCreateFakePlayer = new AdminCreateFakePlayer();
    private final AdminCreateTeleporter adminCreateTeleporter = new AdminCreateTeleporter();
    private final AdminCreateProtection adminCreateProtection = new AdminCreateProtection();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("create")
                .then(adminCreateFakePlayer.register())
                .then(adminCreateTeleporter.register())
                .then(adminCreateProtection.register());
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
