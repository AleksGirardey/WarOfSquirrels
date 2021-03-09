package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.city.set.perm.CitySetPermAlly;
import fr.craftandconquest.warofsquirrels.commands.city.set.perm.CitySetPermOutside;
import fr.craftandconquest.warofsquirrels.commands.city.set.perm.CitySetPermRecruit;
import fr.craftandconquest.warofsquirrels.commands.city.set.perm.CitySetPermResident;
import fr.craftandconquest.warofsquirrels.commands.register.ICommandRegister;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CitySetPerm extends CityAssistantCommandBuilder {
    private final CitySetPermAlly citySetPermAlly = new CitySetPermAlly();
    private final CitySetPermOutside citySetPermOutside = new CitySetPermOutside();
    private final CitySetPermRecruit citySetPermRecruit = new CitySetPermRecruit();
    private final CitySetPermResident citySetPermResident = new CitySetPermResident();

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("perm")
                .executes(this)
                .then(citySetPermAlly.register())
                .then(citySetPermOutside.register())
                .then(citySetPermRecruit.register())
                .then(citySetPermResident.register());
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        return 0;
    }
}
