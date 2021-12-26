package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CitySetResident extends CityMayorOrAssistantCommandBuilder implements IPlayerExtractor, IAdminCommand {
    public CitySetResident() {}

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("resident")
                .then(getPlayerRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (player.isAdminMode()) return true;

        FullPlayer argument = getPlayer(context);
        WarOfSquirrels.LOGGER.info("SET RESIDENT 1");

        if (argument == null) {
            player.sendMessage(ChatText.Error("Player doest not exist"));
            return false;
        }

        WarOfSquirrels.LOGGER.info("SET RESIDENT 2");

        return argument.getCity().equals(player.getCity()) && !player.getCity().getOwner().equals(argument);
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        WarOfSquirrels.LOGGER.info("SET RESIDENT 11");
        FullPlayer newResident = getPlayer(context);
        WarOfSquirrels.LOGGER.info("SET RESIDENT 12");

        newResident.setResident(true);
        WarOfSquirrels.LOGGER.info("SET RESIDENT 13");

        if (newResident.getAssistant()) {
            WarOfSquirrels.LOGGER.info("SET RESIDENT 13");
            newResident.setAssistant(false);
        }

        MutableComponent message = ChatText.Colored(newResident.getDisplayName() + " is now resident in " + player.getCity().getDisplayName() + ".", ChatFormatting.GOLD);
            WarOfSquirrels.LOGGER.info("SET RESIDENT 14");

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(newResident.getCity(), null, message, true);
            WarOfSquirrels.LOGGER.info("SET RESIDENT 15");

        return 0;
    }
}
