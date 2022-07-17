package fr.craftandconquest.warofsquirrels.commands.guild;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.arguments.GuildArgumentType;
import fr.craftandconquest.warofsquirrels.commands.extractor.IGuildExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.guild.Guild;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class GuildInfoCommand extends CommandBuilder implements IGuildExtractor {
    private final static GuildInfoCommand NO_ARGS = new GuildInfoCommand(true);

    private final boolean hasArgs;

    public GuildInfoCommand(boolean hasArgs) {
        this.hasArgs = hasArgs;
    }
    public GuildInfoCommand() { this(false); }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("info")
                .executes(this)
                .then(Commands.argument("guild", GuildArgumentType.guild()).executes(NO_ARGS));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {

        if (hasArgs) {
            if (getArgument(context) == null) {
                player.sendMessage(ChatText.Error("There is not guild named '" + getRawArgument(context) + "'"));
                return false;
            }
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Guild guild;

        if (hasArgs)
            guild = getArgument(context);
        else
            guild = player.getGuild();

        guild.displayInfo(player);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }

    @Override
    public boolean isSuggestionFactionRestricted() { return false; }
}
