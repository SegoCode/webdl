package org.segocode.service;

import org.segocode.system.util.FileUtil;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import org.slf4j.Logger;


public class VideoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoService.class);

    /**
     * Sends a video to a specific chat in Telegram.
     *
     * @param chatId           The ID of the chat where the video will be sent.
     * @param replyToMessageId The ID of the message to which this video will be a reply.
     * @return The SendVideo object configured with the chat ID and reply to message ID.
     */
    public static SendVideo sendVideo(Long chatId, Integer replyToMessageId) {
            String filePath = buildFilePath(replyToMessageId);
            LOGGER.info("Locating video file for message ID {}: {}", replyToMessageId, filePath);

            File videoFile = FileUtil.locateVideoFile(filePath);
            LOGGER.info("Video file located successfully: {}", videoFile.getPath());

            SendVideo sendVideoRequest = createSendVideoRequest(chatId, replyToMessageId, videoFile);
            LOGGER.info("Video is sending. Chat ID: {}, Reply to Message ID: {}", chatId, replyToMessageId);

            return sendVideoRequest;
    }

    /**
     * Creates a SendVideo request for sending a video.
     *
     * @param chatId           The ID of the chat where the video will be sent.
     * @param replyToMessageId The ID of the message to which this video will be a reply.
     * @param videoFile        The video file to be sent.
     * @return A SendVideo object configured with the chat ID, reply ID, and video file.
     */
    private static SendVideo createSendVideoRequest(Long chatId, Integer replyToMessageId, File videoFile) {
        SendVideo sendVideoRequest = new SendVideo();
        sendVideoRequest.setChatId(chatId.toString());
        sendVideoRequest.setReplyToMessageId(replyToMessageId);
        sendVideoRequest.setVideo(new InputFile(videoFile));
        return sendVideoRequest;
    }

    /**
     * Builds the file path for the video file based on the given replyToMessageId.
     *
     * @param replyToMessageId The ID of the message to which this video will be a reply.
     * @return The file path as a String.
     */
    private static String buildFilePath(Integer replyToMessageId) {
        return "./downloads/" + replyToMessageId + ".mp4";
    }


}
