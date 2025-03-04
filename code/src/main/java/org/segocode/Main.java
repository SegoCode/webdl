package org.segocode;

import org.segocode.bot.Webdlbot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            LOGGER.info("Starting the video download bot...");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Webdlbot());
            LOGGER.info("Bot started successfully and ready to download videos 🚀");
        } catch (Exception e) {
            LOGGER.error("Error while attempting to start the bot. Error details:", e);
        }
    }
}
