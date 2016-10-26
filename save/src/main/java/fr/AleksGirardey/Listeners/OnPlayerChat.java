package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Channels.GlobalChannel;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

public class OnPlayerChat {
    @Listener
    public void         onPlayerChat(MessageChannelEvent.Chat event) {
        Player player = event.getCause().<Player>first(Player.class).get();

        Text            message;

        message = Text.builder().append(Text.of(Utils.getChatTag(player)), Text.of(" "), event.getRawMessage()).build();

        if (event.getChannel().get() instanceof GlobalChannel) {
            for (MessageReceiver mr : event.getChannel().get().getMembers()) {
                Player p = (Player) mr;
                if (p.getLocation().getPosition().distance(player.getLocation().getPosition()) <= 30)
                    p.sendMessage(message);
            }
        } else
            event.getChannel().get().send(message);
        event.setCancelled(true);
    }
}
