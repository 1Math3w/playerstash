package dev.math3w.playerstash.utils;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Pattern hexColorPattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static TextComponent colorizeComponent(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        Matcher matcher = hexColorPattern.matcher(message);
        List<TextComponent> components = new ArrayList<>();
        int prevEnd = 0;

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            if (start > prevEnd) {
                String plainText = message.substring(prevEnd, start);
                TextComponent textComponent = new TextComponent(plainText);
                components.add(textComponent);
            }

            String colorCode = message.substring(start, end);
            int nextColorCodeIndex = getNextColorCodeIndex(message, end);
            TextComponent colorComponent = new TextComponent(message.substring(end, nextColorCodeIndex));
            colorComponent.setColor(net.md_5.bungee.api.ChatColor.of(colorCode));
            components.add(colorComponent);

            prevEnd = nextColorCodeIndex;
        }

        if (prevEnd < message.length()) {
            String remainingText = message.substring(prevEnd);
            TextComponent textComponent = new TextComponent(remainingText);
            components.add(textComponent);
        }

        TextComponent finalComponent = new TextComponent();
        for (TextComponent component : components) {
            finalComponent.addExtra(component);
        }

        return finalComponent;
    }

    private static int getNextColorCodeIndex(String message, int startIndex) {
        Matcher matcher = hexColorPattern.matcher(message);
        while (matcher.find(startIndex)) {
            if (matcher.start() != startIndex) {
                return matcher.start();
            }
        }
        return message.length();
    }

    public static String colorize(String message) {
        Matcher matcher = hexColorPattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, "" + net.md_5.bungee.api.ChatColor.of(color));
            matcher = hexColorPattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
