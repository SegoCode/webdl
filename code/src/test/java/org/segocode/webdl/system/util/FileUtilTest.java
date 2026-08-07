package org.segocode.webdl.system.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileUtilTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void locatesMp4Download() throws Exception {
        Path video = Files.createFile(temporaryDirectory.resolve("42.mp4"));

        assertEquals(
                video.toFile(),
                FileUtil.locateVideoFile(temporaryDirectory.resolve("42").toString()));
    }

    @Test
    void ignoresNonMp4AndPartialDownloads() throws Exception {
        Files.createFile(temporaryDirectory.resolve("42.webm"));
        Files.createFile(temporaryDirectory.resolve("42.webm.part"));
        Files.createFile(temporaryDirectory.resolve("42.info.json"));

        assertNull(FileUtil.locateVideoFile(temporaryDirectory.resolve("42").toString()));
    }

    @Test
    void removesAllContentsButKeepsDownloadDirectory() throws Exception {
        Path nestedDirectory = Files.createDirectory(temporaryDirectory.resolve("nested"));
        Files.createFile(nestedDirectory.resolve("video.mp4"));
        Files.createFile(temporaryDirectory.resolve("other.webm"));

        FileUtil.cleanDirectory(temporaryDirectory);

        assertTrue(Files.isDirectory(temporaryDirectory));
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(0, files.count());
        }
    }
}
