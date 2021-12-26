package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CityAdd extends CityMayorOrAssistantCommandBuilder implements IPlayerExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("add").then(getPlayerRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getPlayer(context);
        MutableComponent message;
        boolean ret = true;

        if (target == null) {
            message = ChatText.Error("Le joueur " + getRawPlayer(context).getDisplayName() + " n'existe pas.");
            ret = false;
        } else if (target.getCity() != null) {
            message = ChatText.Error("Le joueur " + target.getDisplayName() + " appartient déjà à une ville.");
            ret = false;
        } else
            message = ChatText.Error("Unknown error");

        if (ret) return true;

        player.sendMessage(message);
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getPlayer(context);
        player.getCity().addCitizen(target);
        return 0;
    }
}
