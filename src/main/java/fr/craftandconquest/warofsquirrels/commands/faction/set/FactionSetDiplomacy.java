package fr.craftandconquest.warofsquirrels.commands.faction.set;

import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.extractor.IFactionExtractor;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPermissionExtractor;
import fr.craftandconquest.warofsquirrels.commands.faction.FactionCommandAssistant;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;

public abstract class FactionSetDiplomacy extends FactionCommandAssistant implements IFactionExtractor, IPermissionExtractor {
    protected abstract void NewDiplomacy(FullPlayer player, Faction faction, Permission perm);

    public void Annouce(Faction factionA, Faction factionB, String relation) {
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(ChatText.Colored(
                "[Diplomatie] " + factionA.getDisplayName()
                        + " considère désormais "
                        + factionB.getDisplayName()
                        + " en tant que " + relation + ".", ChatFormatting.GOLD));
    }

    public boolean hasArgs;

    public FactionSetDiplomacy(boolean _hasArgs) {
        hasArgs = _hasArgs;
    }

    public FactionSetDiplomacy() {
        this(false);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return player.getCity().getFaction() != getFaction(context);
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Faction faction = getFaction(context);
        Permission permission;

        if (hasArgs)
            permission = getPermission(context);
        else
            permission = new Permission(false, false, false, false, false);

        NewDiplomacy(player, faction, permission);
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
