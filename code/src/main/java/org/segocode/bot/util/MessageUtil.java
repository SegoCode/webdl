package org.segocode.bot.util;

public class MessageUtil {
    public static String extractUrlFromMessage(String messageText) {
        int startIndex = messageText.indexOf("http");
        int endIndex = messageText.indexOf(" ", startIndex);
        if (endIndex == -1) {
            endIndex = messageText.length();
        }
        return messageText.substring(startIndex, endIndex);
    }
}
