package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.set.*;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CitySet extends CityAssistantCommandBuilder {
    private final CitySetSpawn citySetSpawn = new CitySetSpawn();
    private final CitySetHomeBlock citySetHomeBlock = new CitySetHomeBlock();
    private final CitySetMayor citySetMayor = new CitySetMayor();
    private final CitySetAssistant citySetAssistant = new CitySetAssistant();
    private final CitySetResident citySetResident = new CitySetResident();
    private final CitySetRecruit citySetRecruit = new CitySetRecruit();
    private final CitySetPerm citySetPerm = new CitySetPerm();

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("set")
                .executes(this)
                .then(citySetSpawn.register())
                .then(citySetHomeBlock.register())
                .then(citySetMayor.register())
                .then(citySetAssistant.register())
                .then(citySetResident.register())
                .then(citySetRecruit.register())
                .then(citySetPerm.register());
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        player.getPlayerEntity().sendMessage(new StringTextComponent(
                "--==| city set help |==--\n"
                        + "/city set spawn"
                        + "/city set mayor [player]"
                        + "/city set assistant [player]")
                .applyTextStyle(TextFormatting.GREEN));
        return 0;
    }
}
