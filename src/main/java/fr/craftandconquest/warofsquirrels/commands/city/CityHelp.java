package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CityHelp extends CommandBuilder {
    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        PlayerEntity entity = player.getPlayerEntity();

        StringTextComponent message = new StringTextComponent(
                "--==| city help |==--\n" +
                "/city create [name]\n" +
                "/city info <name>\n" +
                "/city claim\n" +
                "/city unclaim\n" +
                "/city set ...");

        entity.sendMessage(message);

        return 0;
    }

    /** No Implementations needed **/

    @Override
    public LiteralArgumentBuilder<CommandSource> register() { return null; }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) { return true; }


    @Override
    protected ITextComponent ErrorMessage() { return null; }
}
