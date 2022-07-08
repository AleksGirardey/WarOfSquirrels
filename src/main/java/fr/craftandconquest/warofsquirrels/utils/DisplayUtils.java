package fr.craftandconquest.warofsquirrels.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Function;

public class DisplayUtils {
    public static void DisplayTitle(ServerPlayer player, Component text) {
        Function<Component, Packet<?>> packet = ClientboundSetTitleTextPacket::new;

        player.connection.send(packet.apply(text));
    }
}
