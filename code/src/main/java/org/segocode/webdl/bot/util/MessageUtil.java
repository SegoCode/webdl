package org.segocode.webdl.bot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");

    public static String extractUrlFromMessage(String messageText) {
        Matcher matcher = URL_PATTERN.matcher(messageText);
        return matcher.find() ? matcher.group() : null;
    }
}
