package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class CityList extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("list").executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        List<City> cityList = WarOfSquirrels.instance.getCityHandler().getAll();
        StringTextComponent message = new StringTextComponent("");

        for (int i = 0; i < cityList.size(); ++i) {
            message.appendText(cityList.get(i).displayName);
            if (i != cityList.size() - 1)
                message.appendText(", ");
        }

        message.applyTextStyle(TextFormatting.GREEN);
        player.getPlayerEntity().sendMessage(message);
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return new StringTextComponent("SHOULD NOT BE DISPLAYED").applyTextStyle(TextFormatting.RED);
    }
}
