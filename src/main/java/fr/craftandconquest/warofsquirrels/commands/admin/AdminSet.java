package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.admin.set.AdminSetPlayerJoin;
import fr.craftandconquest.warofsquirrels.commands.admin.set.AdminSetSpawn;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminSet extends AdminCommandBuilder {
    private final AdminSetSpawn adminSetSpawn = new AdminSetSpawn();
    private final AdminSetPlayerJoin adminSetPlayerJoin = new AdminSetPlayerJoin();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("set")
                .then(adminSetSpawn.register())
                .then(adminSetPlayerJoin.register())
                .executes(this);
    }

    @Override protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) { return true; }
    @Override protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) { return 0; }
}
