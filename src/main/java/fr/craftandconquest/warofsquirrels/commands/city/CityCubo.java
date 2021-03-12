package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CityCubo extends CityAssistantCommandBuilder {
    private final CityCuboList cityCuboList = new CityCuboList();
    private final CityCuboLeave cityCuboLeave = new CityCuboLeave();
    private final CityCuboCreate cityCuboCreate = new CityCuboCreate();
    private final CityCuboDelete cityCuboDelete = new CityCuboDelete();
    private final CityCuboAdd cityCuboAdd = new CityCuboAdd();
    private final CityCuboRemove cityCuboRemove = new CityCuboRemove();
    private final CityCuboSet cityCuboSet = new CityCuboSet();

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("cubo")
                .executes(this)
                .then(cityCuboList.register())
                .then(cityCuboLeave.register())
                .then(cityCuboCreate.register())
                .then(cityCuboDelete.register())
                .then(cityCuboAdd.register())
                .then(cityCuboRemove.register())
                .then(cityCuboSet.register());
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        return 0;
    }
}
