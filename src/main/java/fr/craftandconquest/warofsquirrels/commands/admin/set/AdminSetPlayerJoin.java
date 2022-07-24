package fr.craftandconquest.warofsquirrels.commands.admin.set;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import lombok.SneakyThrows;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class AdminSetPlayerJoin extends AdminCommandBuilder {
    private final String playerArgumentName = "player";
    private final String dateArgumentName = "date";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("playerJoin")
                .then(Commands.argument(playerArgumentName, EntityArgument.player())
                        .then(Commands.argument(dateArgumentName, StringArgumentType.string())
                                .executes(this)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        try {
            DateFormat.getDateInstance().parse(StringArgumentType.getString(context, dateArgumentName));
        } catch (ParseException e) {
            player.sendMessage(ChatText.Error("Couldn't parse date : " + e.getLocalizedMessage()), true);
            return false;
        }

        return true;
    }

    @SneakyThrows
    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Player target = EntityArgument.getPlayer(context, playerArgumentName);
        Date date = DateFormat.getDateInstance().parse(StringArgumentType.getString(context, dateArgumentName));
        FullPlayer targetFullPlayer = WarOfSquirrels.instance.getPlayerHandler().get(target.getUUID());

        targetFullPlayer.setCityJoinDate(date);

        return 0;
    }
}
