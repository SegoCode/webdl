package org.segocode.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public class MessageService {

    /**
     * Sends a text message to a specific chat in Telegram.
     *
     * @param chatId           The ID of the chat where the message will be sent.
     * @param replyToMessageId The ID of the message to which this message will be a reply.
     * @param text             The text content of the message to be sent.
     * @return The SendMessage object configured with the chat ID, text, and reply to message ID.
     */
    public static SendMessage sendTextMessage(Long chatId, Integer replyToMessageId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        message.setReplyToMessageId(replyToMessageId);
        return message;
    }

    /**
     * Deletes a specific message in a Telegram chat.
     *
     * @param chatId    The ID of the chat where the message is located.
     * @param messageId The ID of the message to be deleted.
     * @return A DeleteMessage object configured with the chat ID and message ID.
     */
    public static DeleteMessage deleteMessage(Long chatId, Integer messageId) {
        return new DeleteMessage(chatId.toString(), messageId);
    }
}
