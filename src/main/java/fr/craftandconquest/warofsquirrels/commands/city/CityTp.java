package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.tp.CityTpHome;
import fr.craftandconquest.warofsquirrels.commands.city.tp.CityTpHub;
import fr.craftandconquest.warofsquirrels.commands.city.tp.CityTpWar;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CityTp extends CityCommandBuilder {
    private final CityTpHome cityTpHome = new CityTpHome();
    private final CityTpHub cityTpHub = new CityTpHub();
    private final CityTpWar cityTpWar = new CityTpWar();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("tp")
                .executes(this)
                .then(cityTpHome.register())
                .then(cityTpHub.register())
                .then(cityTpWar.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        MutableComponent message = ChatText.Colored("""
                /city tp home : teleport you to your city spawn
                         war : teleport you to your fortification spawn
                         hub : teleport you to the server hub
                """, ChatFormatting.BLUE);

        player.sendMessage(message);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You cannot perform this command");
    }
}