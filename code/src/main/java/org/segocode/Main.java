package org.segocode;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("🚀 Starting the video download bot...");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new VideoDownloadBot());
            System.out.println("✅ Bot started successfully and ready to download videos.");
        } catch (Exception e) {
            System.err.println("❌ Error while attempting to start the bot. Error details:");
            e.printStackTrace();
            System.out.println("🔍 Please check the configuration and ensure all dependencies are correctly set up.");
        }
    }
}
