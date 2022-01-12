package fr.craftandconquest.warofsquirrels.commands.city.upgrade;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.upgrade.CityUpgrade;
import lombok.AllArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

@AllArgsConstructor
public class CityUpgradeInfo extends CityMayorOrAssistantCommandBuilder {
    private String upgradeTarget;
    private CityUpgrade.UpgradeType upgradeType;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        if (upgradeType == null) return null;
        return Commands.literal(upgradeTarget).executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        MutableComponent message = new TextComponent("");

        City city = player.getCity();

        if (upgradeType != null)
            message.append(city.getCityUpgrade().asString(upgradeType));
        else
            message.append(city.getCityUpgrade().asString());

        player.sendMessage(message);

        return 0;
    }
}
