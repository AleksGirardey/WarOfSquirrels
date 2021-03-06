package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.war.PartyWar;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CityCreate extends CommandBuilder implements IAdminCommand {
    private final String cityNameArgument = "[CityName]";

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("create")
                .then(Commands
                        .argument(cityNameArgument, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        if (IsAdmin(player)) return true;

        PartyWar party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);
        int minPartySize = WarOfSquirrels.instance.getConfig().getMinPartySizeToCreateCity();

        if (super.CanDoIt(player) && party.getLeader().equals(player)) {
            if (party.size() >= minPartySize && party.createCityCheck())
                return true;
            player.getPlayerEntity().sendMessage(new StringTextComponent("Your party must contain " + (minPartySize - 1)
                    + " wanderers in order to create a city").applyTextStyle(TextFormatting.RED));
        }
        player.getPlayerEntity().sendMessage(new StringTextComponent("You need to create a party to create a city"));
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }
}
