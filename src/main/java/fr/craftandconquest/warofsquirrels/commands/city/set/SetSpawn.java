package fr.craftandconquest.warofsquirrels.commands.city.set;

import fr.craftandconquest.warofsquirrels.commands.city.CityCommandAssistant;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Chunk;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public class            SetSpawn extends CityCommandAssistant {
    @Override
    protected boolean   SpecialCheck(DBPlayer player, CommandContext context) {
        Player p = player.getUser().getPlayer().get();
        World world = p.getWorld();
        Chunk chunk = Core.getChunkHandler().get(
                p.getLocation().getBlockX() / 16,
                p.getLocation().getBlockZ() / 16,
                world);

        if (chunk == null || chunk.getCity() != player.getCity() || !chunk.isHomeblock() || !chunk.isOutpost()) {
            player.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas poser le spawn sur cette parcelle.", TextColors.RESET));
            return false;
        }
        return true;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        int                     x, y, z;
        Chunk                   chunk;
        Player                  p = player.getUser().getPlayer().get();
        World                   world = p.getWorld();

        x = p.getLocation().getBlockX();
        y = p.getLocation().getBlockY();
        z = p.getLocation().getBlockZ();

        chunk = Core.getChunkHandler().get(x / 16, z / 16, world);
        chunk.setRespawnX(x);
        chunk.setRespawnY(y);
        chunk.setRespawnZ(z);

        player.sendMessage(Text.of("Spawn set."));

        return CommandResult.success();
    }
}
