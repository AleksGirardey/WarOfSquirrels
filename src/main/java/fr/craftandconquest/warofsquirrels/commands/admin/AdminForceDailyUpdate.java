package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminForceDailyUpdate extends AdminCommandBuilder{
    private final String argumentName = "count";
    private final boolean hasArgs;
    public AdminForceDailyUpdate() { hasArgs = false; }
    public AdminForceDailyUpdate(boolean _hasArgs) { hasArgs = _hasArgs; }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("forcedailyupdate")
                .executes(this)
                .then(Commands
                        .argument(argumentName, IntegerArgumentType.integer(1, 100))
                        .executes(new AdminForceDailyUpdate(true)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        int count = 1;

        if (hasArgs) {
            count = IntegerArgumentType.getInteger(context, argumentName);
        }

        for (int index = 0; index < count; ++index) {
            WarOfSquirrels.instance.getUpdateHandler().DailyUpdate();
        }

        return 0;
    }
}
