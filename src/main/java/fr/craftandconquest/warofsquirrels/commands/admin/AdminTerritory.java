package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.territory.AdminTerritoryInfo;
import fr.craftandconquest.warofsquirrels.commands.admin.territory.AdminTerritoryList;
import fr.craftandconquest.warofsquirrels.commands.admin.territory.AdminTerritoryTp;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class AdminTerritory extends AdminCommandBuilder {
    private final AdminTerritoryList adminTerritoryList = new AdminTerritoryList();
    private final AdminTerritoryTp adminTerritoryTp = new AdminTerritoryTp();
    private final AdminTerritoryInfo adminTerritoryInfo = new AdminTerritoryInfo();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("territory")
                .executes(this)
                .then(adminTerritoryList.register())
                .then(adminTerritoryTp.register())
                .then(adminTerritoryInfo.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        int halfSize = Math.floorDiv(WarOfSquirrels.instance.getConfig().getMapSize(), 2);
        int max = Math.floorDiv(halfSize, WarOfSquirrels.instance.getConfig().getTerritorySize());
        MutableComponent message = ChatText.Colored("", ChatFormatting.BLUE);

        MutableComponent lineOne = ChatText.Colored("", ChatFormatting.BLUE);
        for (int z = max - 1; z > -max; --z) {
            lineOne = ChatText.Colored("", ChatFormatting.BLUE);
            MutableComponent lineTwo = ChatText.Colored("", ChatFormatting.BLUE);
            MutableComponent lineThree = ChatText.Colored("", ChatFormatting.BLUE);
            MutableComponent lineFour = ChatText.Colored("", ChatFormatting.BLUE);

            for (int x = -max; x < max; ++x) {
                Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(x, z);

                if (territory == null) return -1;
                MutableComponent faction = ChatText.Colored(territory.getFaction() == null ?
                        "x" :
                        territory.getFaction().getDisplayName().substring(0, 1), ChatFormatting.GREEN);

                lineOne.append("+---");
                lineTwo.append("|xxx");
                lineThree.append("|x").append(faction).append("x");
                lineFour.append("|xxx");
            }
            lineOne.append("+\n");
            lineTwo.append("|\n");
            lineThree.append("|\n");
            lineFour.append("|\n");

            message.append(lineOne).append(lineTwo).append(lineThree).append(lineFour);
        }

        message.append(lineOne).append("+\n");

        player.sendMessage(message);

        return 0;
    }
}
