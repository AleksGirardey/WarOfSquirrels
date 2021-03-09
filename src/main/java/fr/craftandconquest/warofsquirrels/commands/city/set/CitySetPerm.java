package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.city.set.perm.*;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CitySetPerm extends CityAssistantCommandBuilder {
    private final CitySetPermAlly citySetPermAlly = new CitySetPermAlly();
    private final CitySetPermEnemy citySetPermEnemy = new CitySetPermEnemy();
    private final CitySetPermFaction citySetPermFaction = new CitySetPermFaction();
    private final CitySetPermOutside citySetPermOutside = new CitySetPermOutside();
    private final CitySetPermRecruit citySetPermRecruit = new CitySetPermRecruit();
    private final CitySetPermResident citySetPermResident = new CitySetPermResident();
    private final CitySetPermCustom citySetPermCustom = new CitySetPermCustom();

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
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
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        player.getPlayerEntity().sendMessage(new StringTextComponent(
                "--==| city set perm |==--\n"
                        + "/city set perm ally [build] [container] [switch]"
                        + "/city set perm enemy [build] [container] [switch]"
                        + "/city set perm faction [build] [container] [switch]"
                        + "/city set perm outside [build] [container] [switch]"
                        + "/city set perm recruit [build] [container] [switch]"
                        + "/city set perm resident [build] [container] [switch]")
                .applyTextStyle(TextFormatting.GREEN));
        return 0;
    }
}
