package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.whitelist.AdminWhitelistCity;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class AdminWhitelist extends AdminCommandBuilder {
    private final AdminWhitelistCity adminWhitelistCity = new AdminWhitelistCity();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("whitelist")
                .executes(this)
                .then(adminWhitelistCity.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        List<FullPlayer> list = WarOfSquirrels.instance.getPlayerHandler().getAll()
                .stream().filter(FullPlayer::isWhitelistCityCreator).toList();

        MutableComponent message = ChatText.Success("-= Whitelist City Creator =-\n");

        for (FullPlayer fullPlayer : list) {
            message.append(" - " + fullPlayer.getDisplayName() + "\n");
        }

        player.sendMessage(message);

        return 0;
    }
}
