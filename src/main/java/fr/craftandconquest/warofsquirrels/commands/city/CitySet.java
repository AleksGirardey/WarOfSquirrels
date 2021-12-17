package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.set.*;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CitySet extends CityAssistantCommandBuilder {
    private final CitySetSpawn citySetSpawn = new CitySetSpawn();
    private final CitySetHomeBlock citySetHomeBlock = new CitySetHomeBlock();
    private final CitySetMayor citySetMayor = new CitySetMayor();
    private final CitySetAssistant citySetAssistant = new CitySetAssistant();
    private final CitySetResident citySetResident = new CitySetResident();
    private final CitySetRecruit citySetRecruit = new CitySetRecruit();
    private final CitySetPerm citySetPerm = new CitySetPerm();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
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
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        player.getPlayerEntity().sendMessage(ChatText.Success(
                """
                        --==| city set help |==--
                        /city set spawn
                        /city set mayor [player]
                        /city set assistant [player]"""), Util.NIL_UUID);
        return 0;
    }
}
