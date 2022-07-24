package fr.craftandconquest.warofsquirrels.commands.guild;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.extractor.ITerritoryExtractor;
import fr.craftandconquest.warofsquirrels.commands.faction.FactionCommand;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class GuildCreateCommand extends FactionCommand implements IAdminCommand, ITerritoryExtractor {
    private final String guildNameArgument = "GuildName";
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("create")
                .then(Commands.argument(guildNameArgument, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (!player.isWhitelistGuildCreator() && !player.isAdminMode()) {
            player.sendMessage(ChatText.Error("You cannot create a guild. Please refer to Discord for rules."));
            return false;
        }

        MutableComponent message;
        int x = player.getPlayerEntity().chunkPosition().x;
        int z = player.getPlayerEntity().chunkPosition().z;
        ResourceKey<Level> dimension = player.getPlayerEntity().getCommandSenderWorld().dimension();

        if (player.getGuild() == null) {
            Territory territory = ExtractTerritory(player);
           Utils.CanPlaceGuild(x, z, dimension);
        } else
            message = ChatText.Error("You already belong to a guild.");

        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }

    @Override
    public boolean suggestionIsGlobalWarTarget() { return false; }

    @Override
    public boolean suggestionIsFactionWarTarget() { return false; }
}
