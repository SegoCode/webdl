package org.segocode.webdl.system.command;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutor.class);
    private static final int MAX_RETRIES = 2;
    private static final int TIMEOUT = 120; // seconds
    private static final int TERMINATION_TIMEOUT = 5; // seconds

    /**
     * Executes a command in the system's command line to download a video using yt-dlp.
     *
     * @param url  The URL of the video to be downloaded.
     * @param uuid The unique identifier used to name the downloaded file.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the current thread is interrupted while waiting.
     */
    public static void executeCommand(String url, String uuid) throws Exception {
        String osName = System.getProperty("os.name").toLowerCase();
        String ytDlpCommand = osName.contains("win") ? "yt-dlp.exe" : "yt-dlp"; // This is needed?
        String outputPath = "." + File.separator + "downloads" + File.separator + uuid + ".%(ext)s";

        String[] command = {
            ytDlpCommand, "-q", "--no-playlist", "-S", "res:720", "--recode-video", "mp4", "-o", outputPath, url
        };

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            LOGGER.info("Attempt {} of {}", attempt, MAX_RETRIES);
            Process process;
            try {
                process = new ProcessBuilder(command)
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start();
            } catch (IOException e) {
                throw new IOException("Unable to start yt-dlp", e);
            }

            try {
                if (!process.waitFor(TIMEOUT, TimeUnit.SECONDS)) {
                    terminate(process);
                    LOGGER.warn("Download attempt {} timed out.", attempt);
                } else if (process.exitValue() == 0) {
                    LOGGER.info("Download successful for URL: {}", url);
                    return;
                } else {
                    LOGGER.warn("Download attempt {} failed with exit code {}.", attempt, process.exitValue());
                }
            } catch (InterruptedException e) {
                terminate(process);
                Thread.currentThread().interrupt();
                throw e;
            }
        }
        LOGGER.error("Max retries reached. Command failed for URL: {}", url);
        throw new RuntimeException("Max retries reached. Command failed.");
    }

    private static void terminate(Process process) throws InterruptedException {
        process.descendants().forEach(ProcessHandle::destroyForcibly);
        process.destroyForcibly();
        if (!process.waitFor(TERMINATION_TIMEOUT, TimeUnit.SECONDS)) {
            LOGGER.error("yt-dlp did not terminate after {} seconds", TERMINATION_TIMEOUT);
        }
    }
}
