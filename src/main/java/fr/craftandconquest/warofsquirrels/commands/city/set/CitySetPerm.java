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
        player.getPlayerEntity().sendMessage(ChatText.Success(
                "--==| city set perm |==--\n"
                        + "/city set perm ally [build] [container] [switch]"
                        + "/city set perm enemy [build] [container] [switch]"
                        + "/city set perm faction [build] [container] [switch]"
                        + "/city set perm outside [build] [container] [switch]"
                        + "/city set perm recruit [build] [container] [switch]"
                        + "/city set perm resident [build] [container] [switch]"), Util.NIL_UUID);
        return 0;
    }
}
