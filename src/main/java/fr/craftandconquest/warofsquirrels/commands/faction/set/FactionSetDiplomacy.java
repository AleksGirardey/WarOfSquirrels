package fr.craftandconquest.warofsquirrels.commands.faction.set;

import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.extractor.IFactionExtractor;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPermissionExtractor;
import fr.craftandconquest.warofsquirrels.commands.faction.FactionCommandAssistant;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public abstract class FactionSetDiplomacy extends FactionCommandAssistant implements IFactionExtractor, IPermissionExtractor {
    protected abstract void     NewDiplomacy(Player player, Faction faction, Permission perm);

    public void Annouce(Faction factionA, Faction factionB, String relation) {
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(new StringTextComponent(
                "[Diplomatie] " + factionA.getDisplayName()
                + " considère désormais "
                + factionB.getDisplayName()
                + " en tant que " + relation + ".").applyTextStyle(TextFormatting.GOLD));
    }

    public boolean hasArgs;

    public FactionSetDiplomacy(boolean _hasArgs) {
        hasArgs = _hasArgs;
    }

    public FactionSetDiplomacy() {
        this(false);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return player.getCity().getFaction() != getFaction(player, context);
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        Faction faction = getFaction(player, context);
        Permission permission;

        if (hasArgs)
            permission = getPermission(player, context);
        else
            permission = new Permission(false, false, false, false ,false);

        NewDiplomacy(player, faction, permission);
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }
}
