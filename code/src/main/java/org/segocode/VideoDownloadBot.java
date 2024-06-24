package org.segocode;

import org.apache.commons.validator.routines.UrlValidator;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.*;

public class VideoDownloadBot extends TelegramLongPollingBot {
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private final UrlValidator urlValidator = new UrlValidator();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public String getBotUsername() {
        return "webdl";
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String username = message.getFrom().getUserName();
            String messageText = message.getText();
            long chatId = message.getChatId();
            int messageId = message.getMessageId();

            System.out.println("📩 Message received from @" + username + ": " + messageText);

            if (urlValidator.isValid(messageText)) {
                int procesandoMessageId = sendTextMessage(chatId, messageId, "🔄 Download request launch...");
                Runnable task = () -> {
                    try {
                        UUID uuid = UUID.randomUUID();
                        System.out.println("🎥 Starting download for @" + username + " with UUID: " + uuid.toString());
                        executeCommand(messageText, uuid.toString());
                        deleteMessage(chatId, procesandoMessageId);
                        sendVideo(chatId, messageId, "./downloads/" + uuid + ".mp4");
                        System.out.println("🗑️ Deleting temporary file for @" + username + " with UUID: " + uuid.toString());
                        deleteFile("./downloads/" + uuid + ".mp4");
                        System.out.println("✅ Download and cleanup completed for @" + username);
                    } catch (IOException | InterruptedException e) {
                        System.err.println("❌ Error processing download request for @" + username + ":");
                        e.printStackTrace();
                    }
                };
                executorService.submit(task);
            } else {
                sendTextMessage(chatId, messageId, "❌ Oops! that doesn't seem to be a valid link.");
                System.err.println("❌ Invalid URL received from @" + username + ": " + messageText);
            }
        }
    }

    /**
     * This method is used to send a text message to a specific chat in Telegram.
     *
     * @param chatId           The ID of the chat where the message will be sent.
     * @param replyToMessageId The ID of the message to which this message will be a reply.
     * @param text             The text content of the message to be sent.
     * @return The ID of the sent message. If an error occurs during the execution, it returns -1.
     */
    private int sendTextMessage(Long chatId, Integer replyToMessageId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyToMessageId(replyToMessageId);
        try {
            Message sentMessage = execute(message);
            return sentMessage.getMessageId();
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return an invalid ID in case of error
        }
    }

    /**
     * This method is used to delete a specific message in a Telegram chat.
     *
     * @param chatId    The ID of the chat where the message is located.
     * @param messageId The ID of the message to be deleted.
     */
    private void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage(); // Create a new DeleteMessage object
        deleteMessage.setChatId(chatId.toString()); // Set the chat ID
        deleteMessage.setMessageId(messageId); // Set the message ID
        try {
            execute(deleteMessage); // Execute the deletion of the message
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to send a video to a specific chat in Telegram.
     *
     * @param chatId           The ID of the chat where the video will be sent.
     * @param replyToMessageId The ID of the message to which this video will be a reply.
     * @param filePath         The path of the video file to be sent.
     */
    private void sendVideo(Long chatId, Integer replyToMessageId, String filePath) {
        File videoFile = new File(filePath);
        if (videoFile.exists()) {
            System.out.println("🎬 Preparing to send video: " + filePath);
            SendVideo sendVideoRequest = new SendVideo(); // Create a new SendVideo object
            sendVideoRequest.setChatId(chatId.toString()); // Set the chat ID
            sendVideoRequest.setReplyToMessageId(replyToMessageId); // Set the ID of the message to which this video will be a reply
            sendVideoRequest.setVideo(new InputFile(videoFile)); // Set the video file to be sent
            try {
                execute(sendVideoRequest); // Execute the sending of the video
                System.out.println("✅ Video sent successfully to chat ID: " + chatId);
            } catch (Exception e) {
                System.err.println("❌ Error while sending video: " + filePath);
                e.printStackTrace();
            }
        } else {
            sendTextMessage(chatId, replyToMessageId, "❌ Unable to download the video");
            System.err.println("❌ File not found: " + filePath);
        }
    }


    /**
     * This method is used to execute a command in the system's command line.
     * Specifically, it uses the yt-dlp command to download a video from a given URL and save it to a specific location.
     *
     * @param url  The URL of the video to be downloaded.
     * @param UUID The unique identifier used to name the downloaded file.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the current thread is interrupted by another thread while it is waiting.
     */
    private void executeCommand(String url, String UUID) throws IOException, InterruptedException {
        System.out.println("⬇️ Starting video download from URL: " + url);
        String command = "yt-dlp -S ext -o ./downloads/" + UUID + " " + url;
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", command);

        try {
            Process process = processBuilder.start();
            System.out.println("⌛ Executing command: " + command);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("✅ Video downloaded successfully. Saved as: ./downloads/" + UUID + ".mp4");
            } else {
                System.err.println("❌ Failed to download video. Command exited with code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("⚠️ Error occurred while executing command:");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * This method is used to delete a file from the system.
     *
     * @param filePath The path of the file to be deleted.
     *                 It first checks if the file exists. If it does, it attempts to delete it.
     *                 If the deletion is successful, it prints a success message.
     *                 If the deletion fails, it prints a failure message.
     *                 If the file does not exist, it prints a file not found message.
     */
    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("🗑️ File " + filePath + " deleted successfully.");
            } else {
                System.err.println("❌ Failed to delete the file " + filePath + ".");
            }
        } else {
            System.out.println("🔍 File " + filePath + " not found.");
        }
    }

}
