package fr.craftandconquest.warofsquirrels.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;

public class ChatText {
    public static MutableComponent Success(String text) {
        return Colored(text, ChatFormatting.GREEN);
    }

    public static MutableComponent Error(String text) {
        return Colored(text, ChatFormatting.RED);
    }

    public static MutableComponent Colored(String text, ChatFormatting color) {
        return MutableComponent.create(ComponentContents.EMPTY).append(text).withStyle(color);
    }
}
