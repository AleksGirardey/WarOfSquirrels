package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class CityList extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("list").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        List<City> cityList = WarOfSquirrels.instance.getCityHandler().getAll();
        MutableComponent message = ChatText.Success("");

        for (int i = 0; i < cityList.size(); ++i) {
            message.append(cityList.get(i).displayName);
            if (i != cityList.size() - 1)
                message.append(", ");
        }
        player.getPlayerEntity().sendMessage(message, Util.NIL_UUID);
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("SHOULD NOT BE DISPLAYED");
    }
}
