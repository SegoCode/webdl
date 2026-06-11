package org.segocode.webdl;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.segocode.webdl.bot.Webdlbot;
import org.segocode.webdl.panel.PanelApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            LOGGER.info("Starting storage manager...");
            final EmbeddedStorageManager storageManager = EmbeddedStorage.start();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            LOGGER.info("Starting the video download bot...");
            botsApi.registerBot(new Webdlbot(storageManager));
            LOGGER.info("Bot started successfully and ready to download videos 🚀");
            LOGGER.info("Starting webp anel app...");
            PanelApplication.start(storageManager);
        } catch (Exception e) {
            LOGGER.error("Error while attempting to start the bot. Error details:", e);
        }
    }
}
