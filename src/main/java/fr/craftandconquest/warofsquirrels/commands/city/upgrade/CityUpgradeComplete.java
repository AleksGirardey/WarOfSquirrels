package fr.craftandconquest.warofsquirrels.commands.city.upgrade;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.upgrade.city.CityUpgrade;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import lombok.AllArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

@AllArgsConstructor
public class CityUpgradeComplete extends CityMayorOrAssistantCommandBuilder {
    private String upgradeTarget;
    private CityUpgrade.UpgradeType upgradeType;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal(upgradeTarget).executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city = player.getCity();
        boolean complete = city.getCityUpgrade().CompleteUpgrade(upgradeType, player.isAdminMode());

        if (complete) {
            WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(
                    city,
                    null,
                    ChatText.Success("City " + upgradeType + " has been completed [" + city.getCityUpgrade().getUpgradeInfo(upgradeType).getCurrentLevel() + "/4]"),
                    true);
        } else {
            player.sendMessage(ChatText.Error("Cannot complete " + upgradeType + " upgrade."));
        }

        return 0;
    }
}
