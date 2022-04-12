package fr.craftandconquest.warofsquirrels.commands.city.bastion;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.CityBastionCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.city.bastion.upgrade.CityBastionChestLocation;
import fr.craftandconquest.warofsquirrels.commands.city.bastion.upgrade.CityBastionUpgradeComplete;
import fr.craftandconquest.warofsquirrels.commands.city.bastion.upgrade.CityBastionUpgradeFill;
import fr.craftandconquest.warofsquirrels.commands.city.bastion.upgrade.CityBastionUpgradeInfo;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.upgrade.BastionUpgrade;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CityBastionUpgrade extends CityBastionCommandBuilder {
    private final CityBastionChestLocation chest = new CityBastionChestLocation();
    /** Info **/
    private final CityBastionUpgradeInfo info = new CityBastionUpgradeInfo("", null);
    private final CityBastionUpgradeInfo infoLevel = new CityBastionUpgradeInfo("level", BastionUpgrade.UpgradeType.Level);
    private final CityBastionUpgradeInfo infoVillage = new CityBastionUpgradeInfo("village", BastionUpgrade.UpgradeType.Village);
    private final CityBastionUpgradeInfo infoBarrack = new CityBastionUpgradeInfo("barrack", BastionUpgrade.UpgradeType.Barrack);
    private final CityBastionUpgradeInfo infoFortification = new CityBastionUpgradeInfo("fortification", BastionUpgrade.UpgradeType.Fortification);
    private final CityBastionUpgradeInfo infoRoad = new CityBastionUpgradeInfo("road", BastionUpgrade.UpgradeType.Road);
    /** Fill **/
    private final CityBastionUpgradeFill fillLevel = new CityBastionUpgradeFill("level", BastionUpgrade.UpgradeType.Level);
    private final CityBastionUpgradeFill fillVillage = new CityBastionUpgradeFill("village", BastionUpgrade.UpgradeType.Village);
    private final CityBastionUpgradeFill fillBarrack = new CityBastionUpgradeFill("barrack", BastionUpgrade.UpgradeType.Barrack);
    private final CityBastionUpgradeFill fillFortification = new CityBastionUpgradeFill("fortification", BastionUpgrade.UpgradeType.Fortification);
    private final CityBastionUpgradeFill fillRoad = new CityBastionUpgradeFill("road", BastionUpgrade.UpgradeType.Road);
    /** Complete **/
    private final CityBastionUpgradeComplete completeLevel = new CityBastionUpgradeComplete("level", BastionUpgrade.UpgradeType.Level);
    private final CityBastionUpgradeComplete completeVillage = new CityBastionUpgradeComplete("village", BastionUpgrade.UpgradeType.Village);
    private final CityBastionUpgradeComplete completeBarrack = new CityBastionUpgradeComplete("barrack", BastionUpgrade.UpgradeType.Barrack);
    private final CityBastionUpgradeComplete completeFortification = new CityBastionUpgradeComplete("fortification", BastionUpgrade.UpgradeType.Fortification);
    private final CityBastionUpgradeComplete completeRoad = new CityBastionUpgradeComplete("road", BastionUpgrade.UpgradeType.Road);

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("upgrade")
                .executes(this)
                .then(Commands.literal("upgradeChestLocation")
                        .executes(chest))
                .then(Commands.literal("info")
                        .executes(info)
                        .then(infoLevel.register())
                        .then(infoVillage.register())
                        .then(infoBarrack.register())
                        .then(infoFortification.register())
                        .then(infoRoad.register()))
                .then(Commands.literal("fill")
                        .then(fillLevel.register())
                        .then(fillVillage.register())
                        .then(fillBarrack.register())
                        .then(fillFortification.register())
                        .then(fillRoad.register()))
                .then(Commands.literal("complete")
                        .then(completeLevel.register())
                        .then(completeVillage.register())
                        .then(completeBarrack.register())
                        .then(completeFortification.register())
                        .then(completeRoad.register()));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        MutableComponent message = ChatText.Success("""
                /.. upgrade info [UpgradeType]
                /.. upgrade fill [UpgradeType]
                /.. upgrade complete [UpgradeType]
                """);

        player.sendMessage(message);

        return 0;
    }
}
