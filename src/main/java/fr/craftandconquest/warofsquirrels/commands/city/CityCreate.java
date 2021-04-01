package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.extractor.ITerritoryExtractor;
import fr.craftandconquest.warofsquirrels.handler.ChunkHandler;
import fr.craftandconquest.warofsquirrels.handler.CityHandler;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.channels.CityChannel;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.dimension.DimensionType;

public class CityCreate extends CommandBuilder implements IAdminCommand, ITerritoryExtractor {
    private final String cityNameArgument = "[CityName]";

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("create")
                .then(Commands
                        .argument(cityNameArgument, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean CanDoIt(Player player) {
        if (IsAdmin(player)) return true;

        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);
        int minPartySize = WarOfSquirrels.instance.getConfig().getMinPartySizeToCreateCity();

        if (super.CanDoIt(player) && party.getLeader().equals(player)) {
            if (party.size() >= minPartySize && party.createCityCheck())
                return true;
            player.getPlayerEntity().sendMessage(new StringTextComponent("Your party must contain " + (minPartySize - 1)
                    + " wanderers in order to create a city").applyTextStyle(TextFormatting.RED));
        }
        player.getPlayerEntity().sendMessage(new StringTextComponent("You need to create a party to create a city"));
        return false;
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
//        String cityName = context.getArgument(cityNameArgument, String.class);
        StringTextComponent message;
        int x, z;

        x = player.getPlayerEntity().getPosition().getX() / 16;
        z = player.getPlayerEntity().getPosition().getZ() / 16;

        if (player.getCity() == null) {
            Territory territory = ExtractTerritory(player);
            if (!WarOfSquirrels.instance.getChunkHandler().exists(x, z, DimensionType.OVERWORLD)
                    && Utils.CanPlaceCity(x, z)
                    && territory.getFaction() == null && territory.getFortification() == null) {
                return true;
            } else
                message = new StringTextComponent("You can't set a new city here ! Too close from civilization");
        } else
            message = new StringTextComponent("Leave your city first !");

        message.applyTextStyle(TextFormatting.RED);
        player.getPlayerEntity().sendMessage(message);
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        String cityName = context.getArgument(cityNameArgument, String.class);
        CityHandler cih = WarOfSquirrels.instance.getCityHandler();
        ChunkHandler chh = WarOfSquirrels.instance.getChunkHandler();
        City city = cih.CreateCity(cityName, cityName.substring(0, 3), player);
        Chunk chunk;
        Territory territory = ExtractTerritory(player);

        player.setCity(city);
        chunk = chh.CreateChunk(player.getPlayerEntity().chunkCoordX, player.getPlayerEntity().chunkCoordZ, city, player.getPlayerEntity().dimension.getId());
        chh.add(chunk);
        WarOfSquirrels.instance.getBroadCastHandler().AddTarget(city, new CityChannel(city));
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(city, player);

        for (Player p : WarOfSquirrels.instance.getPartyHandler().getPartyFromLeader(player).toList()) {
            p.setCity(city);
            city.addCitizen(p);
            WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(city, p);
        }
        StringTextComponent message = new StringTextComponent("[BREAKING NEWS] " + cityName + " have been created by " + player.getDisplayName());
        message.applyTextStyle(TextFormatting.GOLD);

        territory.SetFortification(city);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);

        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return new StringTextComponent("You cannot create a city.").applyTextStyle(TextFormatting.RED);
    }
}
