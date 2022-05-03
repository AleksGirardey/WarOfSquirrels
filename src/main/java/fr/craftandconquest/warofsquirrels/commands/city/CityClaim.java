package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.claim.CityClaimBastion;
import fr.craftandconquest.warofsquirrels.commands.city.claim.CityClaimChunk;
import fr.craftandconquest.warofsquirrels.commands.city.claim.CityClaimOutpost;
import fr.craftandconquest.warofsquirrels.commands.city.claim.CityClaimRename;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CityClaim extends CityMayorOrAssistantCommandBuilder {
    private final CityClaimBastion cityClaimBastion = new CityClaimBastion();
    private final CityClaimChunk cityClaimChunk = new CityClaimChunk();
    private final CityClaimOutpost cityClaimOutpost = new CityClaimOutpost();
    private final CityClaimRename cityClaimRename = new CityClaimRename();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("claim")
                .then(cityClaimBastion.register())
                .then(cityClaimChunk.register())
                .then(cityClaimOutpost.register())
                .then(cityClaimRename.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return 0;
    }
}
