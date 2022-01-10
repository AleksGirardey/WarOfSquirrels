package fr.craftandconquest.warofsquirrels.commands.city.tp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.ITerritoryExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.SpawnTeleporter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class CityTpHome extends CityCommandBuilder implements ITerritoryExtractor {
    private final static CityTpHome CMD_NO_ARGS = new CityTpHome();
    private final static CityTpHome CMD_ARGS = new CityTpHome(true);

    private final boolean hasArgs;

    public CityTpHome() { hasArgs = false; }
    public CityTpHome(boolean _hasArgs) { hasArgs = _hasArgs; }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("home")
                .executes(CMD_NO_ARGS)
                .then(getTerritoryRegister()
                        .executes(CMD_ARGS));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Territory territory;

        if (hasArgs) {
            territory = getTerritory(context);
        } else {
            territory = WarOfSquirrels.instance.getTerritoryHandler().get(player.getCity());
        }

        if (territory == null) {
            player.sendMessage(ChatText.Error(getRawTerritory(context) + " is not a territory."));
            return false;
        }

        boolean cityHasFaction = player.getCity().getFaction() != null;
        boolean territoryHasFaction = territory.getFaction() != null;
        boolean cityOwnTerritory = territoryHasFaction && cityHasFaction && territory.getFaction().equals(player.getCity().getFaction());

        int palaceLevel = player.getCity().getCityUpgrade().getPalace().getCurrentLevel();

        if (cityOwnTerritory) {
            if (palaceLevel < 2) {
                player.sendMessage(ChatText.Error("Your city palace is not enough high level to allow you to teleport to your fortifications."));
                return false;
            }
        } else {
            boolean cityAlly = territoryHasFaction && cityHasFaction && WarOfSquirrels.instance.getDiplomacyHandler().getAllies(player.getCity().getFaction()).contains(territory.getFaction());

            if (!cityAlly || palaceLevel < 4) {
                player.sendMessage(ChatText.Error("Your city is not allied to territory's owner or your city palace is not enough high level"));
                return false;
            }
        }

        if (player.getRemainingTp() < 1) {
            player.sendMessage(ChatText.Error("You do not have tp credit available."));
            return false;
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Territory territory;

        if (hasArgs) {
            territory = getTerritory(context);
        } else {
            territory = WarOfSquirrels.instance.getTerritoryHandler().get(player.getCity());
        }

        SpawnTeleporter tp = new SpawnTeleporter(territory.getFortification().getSpawn());
        ServerLevel level = WarOfSquirrels.server.getLevel(Level.OVERWORLD);

        if (level == null) {
            player.sendMessage(ChatText.Error("Error while teleporting"));
            return -1;
        }

        player.setRemainingTp(player.getRemainingTp() - 1);
        player.getPlayerEntity().changeDimension(level, tp);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You cannot perform this command");
    }
}
