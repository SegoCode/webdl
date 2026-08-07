package org.segocode.webdl.bot.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class MessageUtilTest {
    @Test
    void extractsFirstHttpUrlFromMessage() {
        assertEquals(
                "https://example.com/video?id=1",
                MessageUtil.extractUrlFromMessage("download https://example.com/video?id=1 now"));
    }

    @Test
    void rejectsTextWithoutHttpUrl() {
        assertNull(MessageUtil.extractUrlFromMessage("example.com/video"));
        assertNull(MessageUtil.extractUrlFromMessage("not a link"));
    }
}
