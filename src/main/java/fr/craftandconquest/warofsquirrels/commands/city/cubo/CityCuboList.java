package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class CityCuboList extends CityCommandBuilder {
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
        List<Cubo> cubos = WarOfSquirrels.instance.getCuboHandler().getCubo(player);

        MutableComponent message = ChatText.Colored("=== Liste de vos cubo(s) [" + cubos.size() + "] ===\n", ChatFormatting.BLUE);

        cubos.forEach(c -> message.append("- ").append(c.display()).append("\n"));

        player.getPlayerEntity().sendMessage(message, Util.NIL_UUID);
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
