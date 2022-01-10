package fr.craftandconquest.warofsquirrels.commands.city.tp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.SpawnTeleporter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;

public class CityTpHub extends CityCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("hub").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        int palaceLevel = player.getCity().getCityUpgrade().getPalace().getCurrentLevel();

        if (palaceLevel < 4) {
            player.sendMessage(ChatText.Error("Your city palace is not enough high level to allow you to teleport to the hub."));
            return false;
        }

        if (player.getRemainingTp() < 1) {
            player.sendMessage(ChatText.Error("You do not have tp credit available."));
            return false;
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        SpawnTeleporter tp = new SpawnTeleporter(WarOfSquirrels.instance.getConfig().getServerSpawn());
        ServerLevel level = WarOfSquirrels.server.getLevel(WarOfSquirrels.SPAWN);

        if (level == null) {
            player.sendMessage(ChatText.Error("Error while teleporting"));
            return -1;
        }

        player.setRemainingTp(player.getRemainingTp() - 1);
        player.getPlayerEntity().changeDimension(level, tp);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You cannot perform this command");
    }
}
