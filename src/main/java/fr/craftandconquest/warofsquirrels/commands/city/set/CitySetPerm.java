package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.city.set.perm.*;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.text.MessageFormat;

public class CitySetPerm extends CityMayorOrAssistantCommandBuilder {
    private final CitySetPermAlly citySetPermAlly = new CitySetPermAlly();
    private final CitySetPermEnemy citySetPermEnemy = new CitySetPermEnemy();
    private final CitySetPermFaction citySetPermFaction = new CitySetPermFaction();
    private final CitySetPermOutside citySetPermOutside = new CitySetPermOutside();
    private final CitySetPermRecruit citySetPermRecruit = new CitySetPermRecruit();
    private final CitySetPermResident citySetPermResident = new CitySetPermResident();
    private final CitySetPermCustom citySetPermCustom = new CitySetPermCustom();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("perm")
                .executes(this)
                .then(citySetPermAlly.register())
                .then(citySetPermEnemy.register())
                .then(citySetPermFaction.register())
                .then(citySetPermOutside.register())
                .then(citySetPermRecruit.register())
                .then(citySetPermResident.register())
                .then(citySetPermCustom.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String perm = "[build] [container] [switch] [farm] [interact]";
        player.sendMessage(ChatText.Success(MessageFormat.format(
                """
                        --==| city set perm |==--
                        /city set perm ally {0}
                        /city set perm enemy {0}
                        /city set perm faction {0}
                        /city set perm outside {0}
                        /city set perm recruit {0}
                        /city set perm resident {0}""", perm)));
        return 0;
    }
}
