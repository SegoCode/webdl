package org.segocode.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // Unique identifiers
    private String id;
    private Long chatId;
    private String userName;

    // Personal information
    private String firstName;
    private String lastName;

    // Preferences and metadata
    private String languageCode;
    private Boolean isPremium;

    // Usage statistics
    private Integer messageCount;
    private String lastMessageTime;

    /**
     * Increments the message count and updates the last message time to current time
     */
    public void recordNewMessage() {
        if (this.messageCount == null) {
            this.messageCount = 0;
        }
        this.messageCount++;
        this.lastMessageTime = LocalDateTime.now().toString();
    }
}