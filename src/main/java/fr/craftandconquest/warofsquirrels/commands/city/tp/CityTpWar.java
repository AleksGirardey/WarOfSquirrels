package fr.craftandconquest.warofsquirrels.commands.city.tp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IWarExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.SpawnTeleporter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;

public class CityTpWar extends CityCommandBuilder implements IWarExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("war")
                .then(getArgumentRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        War war = getArgument(context);
        if (war == null) {
            player.sendMessage(ChatText.Error("Please use attacker/defender name or tag to indicate the war."));
            return false;
        }

        boolean playerInWar = war.contains(player);
        boolean cityDefender = war.contains(player.getCity());
        boolean playerIsDefender = war.isDefender(player);
        int hqLevel = player.getCity().getCityUpgrade().getHeadQuarter().getCurrentLevel();


        if (!playerInWar) {
            player.sendMessage(ChatText.Error("You are not part of this war"));
            return false;
        }

        if (hqLevel < 4) {
            if (cityDefender && playerIsDefender) return true;

            player.sendMessage(ChatText.Error("You cannot use teleport to join this war."));
            return false;
        } else
            return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        War war = getArgument(context);
        SpawnTeleporter tp;

        if (war.isAttacker(player))
            tp = new SpawnTeleporter(war.getAttackerSpawn(player));
        else
            tp = new SpawnTeleporter(war.getDefenderSpawn(player));
        ServerLevel level = WarOfSquirrels.server.getLevel(WarOfSquirrels.SPAWN);

        if (level == null) {
            player.sendMessage(ChatText.Error("Error while teleporting"));
            return -1;
        }
        player.getPlayerEntity().changeDimension(level, tp);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You cannot perform this command");
    }
}
