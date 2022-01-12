package fr.craftandconquest.warofsquirrels.commands.city.bastion.upgrade;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityBastionCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.upgrade.BastionUpgrade;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import lombok.AllArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

@AllArgsConstructor
public class CityBastionUpgradeComplete extends CityBastionCommandBuilder {
    private String upgradeTarget;
    private BastionUpgrade.UpgradeType upgradeType;

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
        Vector2 territoryPos = Utils.WorldToTerritoryCoordinates(player.getPlayerEntity().getBlockX(), player.getPlayerEntity().getBlockZ());
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(territoryPos);
        Bastion bastion = (Bastion) territory.getFortification();
        boolean complete = bastion.getBastionUpgrade().CompleteUpgrade(upgradeType, bastion);

        if (complete) {
            WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(bastion.getRelatedCity(), null,
                    ChatText.Success("Bastion " + upgradeType + " has been completed [" + bastion.getBastionUpgrade().getUpgradeInfo(upgradeType).getCurrentLevel() + "/4]"),
                    true);
        } else
            player.sendMessage(ChatText.Error("Cannot complete " + upgradeType + " upgrade."));

        return 0;
    }
}
