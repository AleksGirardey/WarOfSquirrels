package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class CityCuboList extends CityCommandBuilder {
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
        List<Cubo> cubos = WarOfSquirrels.instance.getCuboHandler().getCubo(player);

        StringTextComponent message = new StringTextComponent("=== Liste de vos cubo(s) [" + cubos.size() + "] ===\n");

        cubos.forEach(c -> message.appendText("- " + c.display() + "\n"));
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }
}
