package org.segocode.webdl.bot;

import static org.segocode.webdl.bot.constants.Messages.*;
import static org.segocode.webdl.bot.util.MessageUtil.*;
import static org.segocode.webdl.system.util.FileUtil.*;

import org.segocode.webdl.bot.service.MessageService;
import org.segocode.webdl.bot.service.VideoService;
import org.segocode.webdl.system.command.CommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Webdlbot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Webdlbot.class);
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");

    private final DownloadQueue downloadQueue = new DownloadQueue();

    @Override
    public String getBotUsername() {
        return "webdl";
    }

    @Override
    public String getBotToken() {
        if (BOT_TOKEN == null || BOT_TOKEN.isBlank()) {
            LOGGER.error("BOT_TOKEN is not set in the environment variables.");
            throw new IllegalStateException("BOT_TOKEN is not set in the environment variables.");
        }
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                int queuedTasks = downloadQueue.pendingTasks();
                Integer queuedMessageId;
                if (queuedTasks > 0) {
                    String messageTime = DOWNLOAD_REQUEST_QUEUED + " (<" + queuedTasks + "m)";
                    LOGGER.info(
                            "Executor is busy, queuing the message from @{}",
                            update.getMessage().getFrom().getUserName());
                    queuedMessageId = execute(MessageService.sendTextMessage(
                                    update.getMessage().getChatId(),
                                    update.getMessage().getMessageId(),
                                    messageTime))
                            .getMessageId();
                } else {
                    queuedMessageId = null;
                }

                downloadQueue.submit(() -> {
                    try {
                        if (queuedMessageId != null) {
                            deleteMessage(update.getMessage().getChatId(), queuedMessageId);
                        }
                        dispatch(update);
                    } catch (Exception e) {
                        LOGGER.error("Failed to launch dispatch, error: {}", e.getMessage(), e);
                        handleDispatchError(update);
                    } finally {
                        cleanDownloadsFolder();
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Failed on onUpdateReceived, error: {}", e.getMessage(), e);
                handleDispatchError(update);
            }
        }
    }

    private void dispatch(Update update) throws Exception {
        Message message = update.getMessage();
        LOGGER.info(
                "Starting message processing from @{}: {}", message.getFrom().getUserName(), message.getText());

        String url = extractUrlFromMessage(update.getMessage().getText());
        if (url == null) {
            execute(MessageService.sendTextMessage(message.getChatId(), message.getMessageId(), NOT_VALID_LINK))
                    .getMessageId();
            return;
        }
        LOGGER.info("Extracted URL: {} from {}", url, message.getFrom().getUserName());

        final Integer responseId = execute(
                        MessageService.sendTextMessage(message.getChatId(), message.getMessageId(), DOWNLOAD_REQUEST))
                .getMessageId();
        try {
            CommandExecutor.executeCommand(url, String.valueOf(message.getMessageId()));
            execute(VideoService.sendVideo(message.getChatId(), message.getMessageId()));
        } finally {
            deleteMessage(message.getChatId(), responseId);
        }
    }

    private void handleDispatchError(Update update) {
        try {
            execute(MessageService.sendTextMessage(
                            update.getMessage().getChatId(),
                            update.getMessage().getMessageId(),
                            DOWNLOAD_REQUEST_ERROR))
                    .getMessageId();
        } catch (TelegramApiException ex) {
            LOGGER.error("Failed to send error message, error: {}", ex.getMessage(), ex);
        }
    }

    private void deleteMessage(Long chatId, Integer messageId) {
        try {
            execute(MessageService.deleteMessage(chatId, messageId));
        } catch (TelegramApiException e) {
            LOGGER.warn("Failed to delete temporary message {}: {}", messageId, e.getMessage());
        }
    }
}
