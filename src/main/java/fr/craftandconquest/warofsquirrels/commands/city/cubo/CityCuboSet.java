package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CityCuboSet extends CommandBuilder {
    private final CityCuboSetInPerm cityCuboSetInPerm = new CityCuboSetInPerm();
    private final CityCuboSetOutPerm cityCuboSetOutPerm = new CityCuboSetOutPerm();
    private final CityCuboSetCustomPerm cityCuboSetCustomPerm = new CityCuboSetCustomPerm();
    private final CityCuboSetOwner cityCuboSetOwner = new CityCuboSetOwner();

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("set")
                .executes(this)
                .then(cityCuboSetInPerm.register())
                .then(cityCuboSetOutPerm.register())
                .then(cityCuboSetCustomPerm.register())
                .then(cityCuboSetOwner.register());
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        player.getPlayerEntity().sendMessage(new StringTextComponent("=== Cubo set ===\n" +
                "... set owner : Défini le propriétaire du cubo\n" +
                "... set inperm : Défini les permissions des joueurs dans la liste\n" +
                "... set outperm : Défini les permissions des joueurs hors de la liste\n" +
                "... set customperm : Défini des permissions spécifiques pour un joueur, une ville ou une faction\n")
                .applyTextStyle(TextFormatting.GREEN));
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }
}
