package fr.craftandconquest.warofsquirrels.commands.city.cubo.set;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Cubo;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class CuboCommandSetOutPerm extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();
        List<DBPlayer> list = new ArrayList<>();

        list.add(cubo.getOwner());
        list.add(cubo.getLoan().getLoaner());
        list.add(cubo.getCity().getOwner());
        list.addAll(cubo.getCity().getAssistants());

        if (list.contains(player))
            return true;

        player.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas modifier les permissions de ce cubo.", TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();
        boolean build = context.<Boolean>getOne(Text.of("[build]")).get(),
                container = context.<Boolean>getOne(Text.of("[container]")).get(),
                switchh = context.<Boolean>getOne(Text.of("[switch]")).get();
        Permission out = cubo.getPermissionOut();

        out.setBuild(build);
        out.setContainer(container);
        out.setSwitch_(switchh);
        cubo.setPermissionIn(out);
        return CommandResult.success();
    }
}
