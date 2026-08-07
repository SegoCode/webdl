package org.segocode.webdl;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.segocode.webdl.bot.Webdlbot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            LOGGER.info("Starting the video download bot...");
            Webdlbot bot = new Webdlbot();
            BotSession session = botsApi.registerBot(bot);
            startWatchdog(bot, session);
            LOGGER.info("Bot started successfully and ready to download videos 🚀");
        } catch (Exception e) {
            LOGGER.error("Error while attempting to start the bot. Error details:", e);
            throw new IllegalStateException("Unable to start the bot", e);
        }
    }

    private static void startWatchdog(Webdlbot bot, BotSession session) {
        AtomicInteger failures = new AtomicInteger();
        Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().factory())
                .scheduleWithFixedDelay(
                        () -> {
                            try {
                                if (!session.isRunning()) {
                                    throw new IllegalStateException("Telegram bot session stopped");
                                }
                                bot.execute(new GetMe());
                                failures.set(0);
                            } catch (Exception e) {
                                int currentFailures = failures.incrementAndGet();
                                LOGGER.warn("Telegram health check failed ({}/3): {}", currentFailures, e.getMessage());
                                if (currentFailures >= 3) {
                                    LOGGER.error("Telegram connection is unhealthy; exiting for Docker restart");
                                    System.exit(1);
                                }
                            }
                        },
                        1,
                        1,
                        TimeUnit.MINUTES);
    }
}
