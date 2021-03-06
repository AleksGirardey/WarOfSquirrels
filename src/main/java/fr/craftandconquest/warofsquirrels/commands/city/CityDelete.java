package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CityDelete extends CityMayorCommandBuilder implements IAdminCommand {
    @Override
    protected boolean CanDoIt(Player player) {
        return super.CanDoIt(player) || IsAdmin(player);
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("delete").executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        if (player.getCity().getFaction() != null) {
            if (player.getCity().getFaction().getCapital().equals(player.getCity())) {
                player.getPlayerEntity()
                        .sendMessage(new StringTextComponent("You cannot delete your faction capital.")
                                .applyTextStyle(TextFormatting.RED));
                return false;
            }
        }
        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        City city = player.getCity();

        StringTextComponent message = new StringTextComponent("[BREAKING NEWS] " + city.getDisplayName() + " has fallen !");
        message.applyTextStyle(TextFormatting.GOLD);
        WarOfSquirrels.instance.getCityHandler().Delete(city);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);

        return 0;
    }
}
