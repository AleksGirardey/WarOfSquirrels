package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.war.AttackTarget;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class DeclareWar extends CityAssistantCommandBuilder {
    private static final DeclareWar CMD = new DeclareWar();

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands
                .literal("attack")
                .then(Commands
                        .argument("cityTargeted", StringArgumentType.string())
                        .executes(CMD));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);
        String target = context.getArgument("cityTargeted", String.class);
        AttackTarget attackTarget = WarOfSquirrels.instance.getCityHandler().getCity(target);

//        if (attackTarget == null) {
//            WarOfSquirrels.instance.getFactionHandler().getBastion(target);
//        }

        if (party == null) {
            player.getPlayerEntity().sendMessage(new StringTextComponent("You need a party to attack. /party create")
                    .applyTextStyle(TextFormatting.RED).applyTextStyle(TextFormatting.BOLD));
            return false;
        }

        for (Player p : party.toList()) {
            if (p.getCity() != party.getLeader().getCity()
                    && (!WarOfSquirrels.instance.getFactionHandler().areEnemies(p.getCity().getFaction(), attackTarget.getFaction())
                    || !WarOfSquirrels.instance.getFactionHandler().areAllies(p.getCity().getFaction(), party.getLeader().getCity().getFaction()))) {
                player.getPlayerEntity().sendMessage(new StringTextComponent("Your party member '" + p.getDisplayName() + "' can't participate to this war.")
                        .applyTextStyle(TextFormatting.RED).applyTextStyle(TextFormatting.BOLD));
                return false;
            }
        }

        if (WarOfSquirrels.instance.getConfig().isPeaceTime())
            player.getPlayerEntity().sendMessage(new StringTextComponent("You cannot declare war in time of peace !!")
                    .applyTextStyle(TextFormatting.DARK_RED).applyTextStyle(TextFormatting.BOLD));
        return (!WarOfSquirrels.instance.getConfig().isPeaceTime());
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        return 0;
    }
}
