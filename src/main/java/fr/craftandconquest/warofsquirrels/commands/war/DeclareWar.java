package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.war.AttackTarget;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class DeclareWar extends CityAssistantCommandBuilder {
    private static final DeclareWar CMD = new DeclareWar();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("attack")
                .then(Commands
                        .argument("cityTargeted", StringArgumentType.string())
                        .executes(CMD));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);
        String target = context.getArgument("cityTargeted", String.class);
        AttackTarget attackTarget = WarOfSquirrels.instance.getCityHandler().getCity(target);

//        if (attackTarget == null) {
//            WarOfSquirrels.instance.getFactionHandler().getBastion(target);
//        }

        if (party == null) {
            player.sendMessage(ChatText.Error("You need a party to attack. /party create")
                    .withStyle(ChatFormatting.BOLD));
            return false;
        }

        for (FullPlayer p : party.toList()) {
            if (p.getCity() != party.getLeader().getCity()
                    && (!WarOfSquirrels.instance.getFactionHandler().areEnemies(p.getCity().getFaction(), attackTarget.getFaction())
                    || !WarOfSquirrels.instance.getFactionHandler().areAllies(p.getCity().getFaction(), party.getLeader().getCity().getFaction()))) {
                player.sendMessage(ChatText.Error("Your party member '" + p.getDisplayName() + "' can't participate to this war.")
                        .withStyle(ChatFormatting.BOLD));
                return false;
            }
        }

        if (WarOfSquirrels.instance.getConfig().isPeaceTime())
            player.sendMessage(ChatText.Error("You cannot declare war in time of peace !!")
                    .withStyle(ChatFormatting.BOLD));
        return (!WarOfSquirrels.instance.getConfig().isPeaceTime());
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return 0;
    }
}
