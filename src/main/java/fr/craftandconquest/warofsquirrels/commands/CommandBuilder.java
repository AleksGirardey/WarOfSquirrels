package fr.craftandconquest.warofsquirrels.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import lombok.SneakyThrows;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public abstract class CommandBuilder implements Command<CommandSource>, IAdminCommand {

    protected String errorMessage = "";

    public abstract LiteralArgumentBuilder<CommandSource> register();

    protected boolean CanDoIt(Player player) { return true; }

    protected abstract boolean  SpecialCheck(Player player, CommandContext<CommandSource> context);

    protected abstract int   ExecCommand(Player player, CommandContext<CommandSource> context) throws Exception;

    protected ITextComponent ErrorMessage(){
        return new StringTextComponent(errorMessage);
    }

    @SneakyThrows
    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity playerEntity = context.getSource().asPlayer();
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity);

        if (IsAdmin(player) || (CanDoIt(player) && SpecialCheck(player, context)))
            return ExecCommand(player, context);

        if(ErrorMessage() != null){
            player.getPlayerEntity().sendMessage(ErrorMessage());
        }
        return -1;
    }
}
