package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.city.cubo.set.CityCuboSetCustomPerm;
import fr.craftandconquest.warofsquirrels.commands.city.cubo.set.CityCuboSetInPerm;
import fr.craftandconquest.warofsquirrels.commands.city.cubo.set.CityCuboSetOutPerm;
import fr.craftandconquest.warofsquirrels.commands.city.cubo.set.CityCuboSetOwner;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CityCuboSet extends CommandBuilder {
    private final CityCuboSetInPerm cityCuboSetInPerm = new CityCuboSetInPerm();
    private final CityCuboSetOutPerm cityCuboSetOutPerm = new CityCuboSetOutPerm();
    private final CityCuboSetCustomPerm cityCuboSetCustomPerm = new CityCuboSetCustomPerm();
    private final CityCuboSetOwner cityCuboSetOwner = new CityCuboSetOwner();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("set")
                .executes(this)
                .then(cityCuboSetInPerm.register())
                .then(cityCuboSetOutPerm.register())
                .then(cityCuboSetCustomPerm.register())
                .then(cityCuboSetOwner.register());
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        player.getPlayerEntity().sendMessage(ChatText.Success("""
                === Cubo set ===
                ... set owner : Défini le propriétaire du cubo
                ... set inperm : Défini les permissions des joueurs dans la liste
                ... set outperm : Défini les permissions des joueurs hors de la liste
                ... set customperm : Défini des permissions spécifiques pour un joueur, une ville ou une faction
                """), Util.NIL_UUID);
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
