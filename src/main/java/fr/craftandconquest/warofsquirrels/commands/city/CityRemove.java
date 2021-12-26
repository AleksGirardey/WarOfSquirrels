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

public class CityRemove extends CityMayorOrAssistantCommandBuilder implements IPlayerExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("remove").then(getPlayerRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getPlayer(context);

        if (target == null || target.getCity() == null || target.getCity().getOwner().equals(target) || !target.getCity().equals(player.getCity())) {
            MutableComponent message = ChatText.Error("Le joueur '").append(getRawPlayer(context).getDisplayName()).append("' n'existe pas ou ne peut pas être expulsé de votre ville.");
            player.sendMessage(message);
            return false;
        }
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getPlayer(context);

        player.getCity().removeCitizen(target, true);
        return 0;
    }
}
