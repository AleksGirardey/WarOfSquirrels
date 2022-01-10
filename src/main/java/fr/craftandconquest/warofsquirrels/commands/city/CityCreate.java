package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.extractor.ITerritoryExtractor;
import fr.craftandconquest.warofsquirrels.commands.party.PartyCommandLeader;
import fr.craftandconquest.warofsquirrels.handler.ChunkHandler;
import fr.craftandconquest.warofsquirrels.handler.CityHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.channels.CityChannel;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;

public class CityCreate extends PartyCommandLeader implements IAdminCommand, ITerritoryExtractor {
    private final String cityNameArgument = "[CityName]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("create")
                .then(Commands
                        .argument(cityNameArgument, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean CanDoIt(FullPlayer player) {
        if (IsAdmin(player)) return true;

        if (player.getLastDimensionKey() != Level.OVERWORLD) return false;

        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);
        int minPartySize = WarOfSquirrels.instance.getConfig().getMinPartySizeToCreateCity();

        if (super.CanDoIt(player) && party.size() >= minPartySize && party.createCityCheck()) return true;

        player.sendMessage(
                ChatText.Error("You need to be in a party of at least " + minPartySize + " wanderers to create a new city"));
        return false;
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        MutableComponent message;
        int x, z;

        x = player.getPlayerEntity().chunkPosition().x;
        z = player.getPlayerEntity().chunkPosition().z;

        if (player.getCity() == null) {
            Territory territory = ExtractTerritory(player);
            if (!WarOfSquirrels.instance.getChunkHandler().exists(x, z, player.getPlayerEntity().getCommandSenderWorld().dimension())
                    && Utils.CanPlaceCity(x, z)
                    && territory.getFaction() == null && territory.getFortification() == null) {
                return true;
            } else
                message = ChatText.Error("You can't set a new city here ! Too close from civilization");
        } else
            message = ChatText.Error("Leave your city first !");

        player.sendMessage(message);
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String cityName = context.getArgument(cityNameArgument, String.class);
        CityHandler cih = WarOfSquirrels.instance.getCityHandler();
        ChunkHandler chh = WarOfSquirrels.instance.getChunkHandler();
        City city = cih.CreateCity(cityName, cityName.substring(0, 3), player);
        Chunk chunk;
        Territory territory = ExtractTerritory(player);

        player.setCity(city);
        chunk = chh.CreateChunk(player.getPlayerEntity().chunkPosition().x, player.getPlayerEntity().chunkPosition().z, city,
                player.getPlayerEntity().getCommandSenderWorld().dimension());
        chunk.setHomeBlock(true);
        chunk.setRespawn(player.getPlayerEntity().getOnPos().above());
        chh.add(chunk);
        WarOfSquirrels.instance.getBroadCastHandler().AddTarget(city, new CityChannel(city));
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(city, player);

        for (FullPlayer p : WarOfSquirrels.instance.getPartyHandler().getPartyFromLeader(player).toList()) {
            p.setCity(city);
            city.addCitizen(p);
            WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(city, p);
        }
        MutableComponent message = ChatText.Colored("[BREAKING NEWS] " + cityName + " have been created by " + player.getDisplayName(),
                ChatFormatting.GOLD);

        territory.SetFortification(city);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);

        player.sendMessage(chunk.creationLogText());

        cih.Save();

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You cannot create a city.");
    }
}
