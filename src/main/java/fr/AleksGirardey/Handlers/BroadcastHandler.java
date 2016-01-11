package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Main;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BroadcastHandler {

    public BroadcastHandler() {
    }

    public void cityChannel(int cityId, String message) {
        String[][]      citizens = Core.getCityHandler().getCitizens(cityId);
        List<String> uuids = new ArrayList<String>();

        for (String[] uuid : citizens)
            uuids.add(uuid[1]);

        for (Player player : Core.getPlugin().getServer().getOnlinePlayers()) {
            if (uuids.contains(player.getUniqueId().toString()))
                player.sendMessage(Text.of(message));
        }
    }
}
