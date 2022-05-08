package fr.craftandconquest.warofsquirrels.commands.faction.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.extractor.ICityExtractor;
import fr.craftandconquest.warofsquirrels.commands.faction.FactionCommandMayor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class FactionSetCapital extends FactionCommandMayor implements ICityExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("capital").then(getArgumentRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (IsAdmin(player)) return true;

        City city = getArgument(context);

        if (city != null && city.getFaction() != null && city.getFaction() == player.getCity().getFaction())
            return true;

        player.sendMessage(ChatText.Error("City '" + getRawArgument(context) + "' does not exist nor belong to you faction."));
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city = getArgument(context);

        WarOfSquirrels.instance.getFactionHandler().SetCapital(player.getCity().getFaction(), city);
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You can't perform this command");
    }

    @Override
    public boolean isSuggestionFactionRestricted() { return true; }
}
