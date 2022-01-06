package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.upgrade.CityUpgradeComplete;
import fr.craftandconquest.warofsquirrels.commands.city.upgrade.CityUpgradeFill;
import fr.craftandconquest.warofsquirrels.commands.city.upgrade.CityUpgradeInfo;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.upgrade.CityUpgrade;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class CityUpgradeCommand extends CityMayorOrAssistantCommandBuilder {
    /** Info **/
    private final CityUpgradeInfo info = new CityUpgradeInfo("", null);
    private final CityUpgradeInfo infoLevel = new CityUpgradeInfo("level", CityUpgrade.UpgradeType.Level);
    private final CityUpgradeInfo infoHousing = new CityUpgradeInfo("housing", CityUpgrade.UpgradeType.Housing);
    private final CityUpgradeInfo infoFacility = new CityUpgradeInfo("facility", CityUpgrade.UpgradeType.Facility);
    private final CityUpgradeInfo infoHeadQuarter = new CityUpgradeInfo("headQuarter", CityUpgrade.UpgradeType.HeadQuarter);
    private final CityUpgradeInfo infoPalace = new CityUpgradeInfo("palace", CityUpgrade.UpgradeType.Palace);
    
    /** Fill **/
    private final CityUpgradeFill fillLevel = new CityUpgradeFill("level", CityUpgrade.UpgradeType.Level);
    private final CityUpgradeFill fillHousing = new CityUpgradeFill("housing", CityUpgrade.UpgradeType.Housing);
    private final CityUpgradeFill fillFacility = new CityUpgradeFill("facility", CityUpgrade.UpgradeType.Facility);
    private final CityUpgradeFill fillHeadQuarter = new CityUpgradeFill("headQuarter", CityUpgrade.UpgradeType.HeadQuarter);
    private final CityUpgradeFill fillPalace = new CityUpgradeFill("palace", CityUpgrade.UpgradeType.Palace);

    /** Complete **/
    private final CityUpgradeComplete completeLevel = new CityUpgradeComplete("level", CityUpgrade.UpgradeType.Level);
    private final CityUpgradeComplete completeHousing = new CityUpgradeComplete("housing", CityUpgrade.UpgradeType.Housing);
    private final CityUpgradeComplete completeFacility = new CityUpgradeComplete("facility", CityUpgrade.UpgradeType.Facility);
    private final CityUpgradeComplete completeHeadQuarter = new CityUpgradeComplete("headQuarter", CityUpgrade.UpgradeType.HeadQuarter);
    private final CityUpgradeComplete completePalace = new CityUpgradeComplete("palace", CityUpgrade.UpgradeType.Palace);

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("upgrade")
                .executes(this)
                .then(Commands.literal("info")
                        .executes(info)
                        .then(infoLevel.register())
                        .then(infoHousing.register())
                        .then(infoFacility.register())
                        .then(infoHeadQuarter.register())
                        .then(infoPalace.register()))
                .then(Commands.literal("fill")
                        .then(fillLevel.register())
                        .then(fillHousing.register())
                        .then(fillFacility.register())
                        .then(fillHeadQuarter.register())
                        .then(fillPalace.register()))
                .then(Commands.literal("complete")
                        .then(completeLevel.register())
                        .then(completeHousing.register())
                        .then(completeFacility.register())
                        .then(completeHeadQuarter.register())
                        .then(completePalace.register()));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) { return true; }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        MutableComponent message = new TextComponent("");

        message.append("""
                /.. upgrade info [UpgradeType]
                /.. upgrade fill [UpgradeType]
                /.. upgrade complete [UpgradeType]
                """);

        player.sendMessage(message);

        return 0;
    }
}
