package org.segocode.bot;

import org.segocode.bot.utils.Utils;
import org.segocode.system.CommandExecutor;
import org.segocode.service.VideoService;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.segocode.service.MessageService;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.*;

import static org.segocode.bot.constants.Messages.*;
import static org.segocode.bot.utils.Utils.*;
import static org.segocode.system.util.FileUtil.*;

public class Webdlbot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Webdlbot.class);
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");

    // Create a ThreadPoolExecutor with a single virtual thread
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
            1, // corePoolSize: the number of threads to keep in the pool, even if they are idle
            1, // maximumPoolSize: the maximum number of threads to allow in the pool
            0L, // keepAliveTime: when the number of threads is greater than the core
            TimeUnit.MILLISECONDS, // the time unit for the keepAliveTime argument
            new LinkedBlockingQueue<>(), // the queue to use for holding tasks before they are executed
            Thread.ofVirtual().factory() // the factory to use when creating new threads
    );

    @Override
    public String getBotUsername() {
        return "webdl";
    }

    @Override
    public String getBotToken() {
        if (BOT_TOKEN == null || BOT_TOKEN.isEmpty()) {
            LOGGER.error("BOT_TOKEN is not set in the environment variables.");
            throw new IllegalStateException("BOT_TOKEN is not set in the environment variables.");
        }
        return BOT_TOKEN;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                Integer queuedMessageId;
                if (executorService.getActiveCount() > 0 || executorService.getQueue().size() > 0) {
                    String messageTime = DOWNLOAD_REQUEST_QUEUED + " (<" + executorService.getQueue().size() + "m)";
                    LOGGER.info("Executor is busy, queuing the message from @{}", update.getMessage().getFrom().getUserName());
                    queuedMessageId = execute(MessageService.sendTextMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), messageTime)).getMessageId();
                } else {
                    queuedMessageId = null;
                }

                executorService.submit(() -> {
                    try {
                        if (queuedMessageId != null) {
                            execute(MessageService.deleteMessage(update.getMessage().getChatId(), queuedMessageId));
                        }
                        //Entry point download flow
                        dispatch(update);
                    } catch (Exception e) {
                        LOGGER.error("Failed to launch dispatch, error: {}", e.getMessage(), e);
                        handleDispatchError(update, e);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Failed on onUpdateReceived, error: {}", e.getMessage(), e);
                handleDispatchError(update, e);
            }
        }
    }

    private void dispatch(Update update) throws Exception {
                String url = "";
                Message message = update.getMessage();
                LOGGER.info("Starting message processing from @{}: {}", message.getFrom().getUserName(), message.getText());

                if(!update.getMessage().toString().contains("http")) {
                    execute(MessageService.sendTextMessage(message.getChatId(), message.getMessageId(), NOT_VALID_LINK)).getMessageId();
                    return;
                } else {
                    url = extractUrlFromMessage(update.getMessage().getText());
                    LOGGER.info("Extracted URL: {} from {}", url, message.getFrom().getUserName());
                }

                final Integer responseId = execute(MessageService.sendTextMessage(message.getChatId(), message.getMessageId(), DOWNLOAD_REQUEST)).getMessageId();
                CommandExecutor.executeCommand(url, String.valueOf(message.getMessageId()));
                execute(VideoService.sendVideo(message.getChatId(), message.getMessageId()));
                execute(MessageService.deleteMessage(message.getChatId(), responseId));
                cleanDownloadsFolder();
    }

    private void handleDispatchError(Update update, Exception e) {
        try {
            execute(MessageService.sendTextMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), DOWNLOAD_REQUEST_ERROR)).getMessageId();
        } catch (TelegramApiException ex) {
            LOGGER.error("Failed to send error message, error: {}", ex.getMessage(), ex);
        } finally {
            cleanDownloadsFolder();
        }
    }
}


