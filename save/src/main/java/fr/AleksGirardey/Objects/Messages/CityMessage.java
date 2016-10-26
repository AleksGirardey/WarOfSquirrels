package fr.AleksGirardey.Objects.Messages;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class CityMessage implements MessageChannel{

    private Player _player;

    public CityMessage(Player player) {
        super();
        _player = player;
    }

    @Override
    public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {
        Text text = original;
        text = Text.of(TextColors.DARK_AQUA, "[" + Core.getCityHandler().<String>getElement(
                Core.getPlayerHandler().<Integer>getElement(_player, "player_cityId"), "city_tag") + "]", TextColors.RESET, text);
        return Optional.of(text);
    }

    @Override
    public Collection<MessageReceiver> getMembers() {
        return Collections.emptyList();
    }
}
