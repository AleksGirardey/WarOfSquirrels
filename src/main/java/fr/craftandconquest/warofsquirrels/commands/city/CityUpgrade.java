package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.upgrade.CityUpgradeFill;
import fr.craftandconquest.warofsquirrels.commands.city.upgrade.CityUpgradeInfo;
import fr.craftandconquest.warofsquirrels.commands.city.upgrade.CityUpgradeUpgrade;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class CityUpgrade extends CityMayorOrAssistantCommandBuilder {
    private final CityUpgradeInfo info = new CityUpgradeInfo();
    private final CityUpgradeFill fill = new CityUpgradeFill();
    private final CityUpgradeUpgrade upgrade = new CityUpgradeUpgrade();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("upgrade")
                .executes(this)
                .then(info.register())
                .then(fill.register())
                .then(upgrade.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        MutableComponent message = new TextComponent("");

        message.append("""
                /.. upgrade info
                /.. upgrade fill
                /.. upgrade upgrade
                """);

        player.sendMessage(message);

        return 0;
    }
}
