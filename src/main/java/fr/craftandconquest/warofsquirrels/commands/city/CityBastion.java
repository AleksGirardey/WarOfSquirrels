package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.bastion.CityBastionDelete;
import fr.craftandconquest.warofsquirrels.commands.city.bastion.CityBastionList;
import fr.craftandconquest.warofsquirrels.commands.city.bastion.CityBastionName;
import fr.craftandconquest.warofsquirrels.commands.city.bastion.CityBastionUpgrade;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CityBastion extends CityMayorOrAssistantCommandBuilder {
    private final CityBastionList cityBastionList = new CityBastionList();
    private final CityBastionDelete cityBastionDelete = new CityBastionDelete();
    private final CityBastionUpgrade cityBastionUpgrade = new CityBastionUpgrade();
    private final CityBastionName cityBastionName = new CityBastionName();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("bastion")
                .executes(this)
                .then(cityBastionList.register())
                .then(cityBastionDelete.register())
                .then(cityBastionName.register())
                .then(cityBastionUpgrade.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        player.sendMessage(ChatText.Success("""
                /city bastion list : list all bastion related to this city
                          ... delete : delete bastion
                          ... name : change bastion name
                          ... upgrade : bastion upgrade commands
                """));
        return 0;
    }
}
