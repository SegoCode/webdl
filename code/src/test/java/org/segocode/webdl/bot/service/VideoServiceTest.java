package org.segocode.webdl.bot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;

class VideoServiceTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void createsTelegramRequestForDownloadedVideo() throws Exception {
        Path video = Files.createFile(temporaryDirectory.resolve("42.mp4"));

        SendVideo request = VideoService.sendVideo(123L, 42, temporaryDirectory);

        assertEquals("123", request.getChatId());
        assertEquals(42, request.getReplyToMessageId());
        assertEquals(video.toFile(), request.getVideo().getNewMediaFile());
    }
}
