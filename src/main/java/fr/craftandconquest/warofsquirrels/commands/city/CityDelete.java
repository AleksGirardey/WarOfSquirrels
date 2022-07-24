package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CityDelete extends CityMayorCommandBuilder implements IAdminCommand {
    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return super.CanDoIt(player) || IsAdmin(player);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("delete").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (player.getCity().getFaction() != null) {
            if (player.getCity().getFaction().getCapital().equals(player.getCity())) {
                player.sendMessage(ChatText.Error("You cannot delete your faction capital."));
                return false;
            }
        }
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city = player.getCity();
        Faction faction = city.getFaction();

        if (!WarOfSquirrels.instance.getCityHandler().Delete(city)) return -1;


        MutableComponent message = ChatText.Colored("[BREAKING NEWS] " + city.getDisplayName() + " has fallen !",
                ChatFormatting.GOLD);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);

        if (faction != null && faction.getCities().size() <= 0)
            WarOfSquirrels.instance.getFactionHandler().Delete(faction);

        return 0;
    }
}
