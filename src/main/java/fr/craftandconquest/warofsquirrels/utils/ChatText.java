package fr.craftandconquest.warofsquirrels.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class ChatText {
    public static MutableComponent Success(String text) {
        return Colored(text, ChatFormatting.GREEN);
    }

    public static MutableComponent Error(String text) {
        return Colored(text, ChatFormatting.RED);
    }

    public static MutableComponent Colored(String text, ChatFormatting color) {
        return new TextComponent(text).withStyle(color);
    }
}
