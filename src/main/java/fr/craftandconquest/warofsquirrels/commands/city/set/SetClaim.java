package fr.craftandconquest.warofsquirrels.commands.city.set;

import fr.craftandconquest.warofsquirrels.commands.city.CityCommandAssistant;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Chunk;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

public class SetClaim extends CityCommandAssistant {

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        Chunk chunk = Core.getChunkHandler().get(player.getLastChunkX(), player.getLastChunkZ(), player.getLastWorld());

        if (chunk != null) {
            return chunk.getCity() == player.getCity();
        }
        player.sendMessage(Text.of("Vous ne pouvez pas donner un nom à ce chunk."));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Core.getChunkHandler().get(player.getLastChunkX(), player.getLastChunkZ(), player.getLastWorld()).setName(context.<String>getOne("[name]").orElse(""));
        return CommandResult.success();
    }
}
