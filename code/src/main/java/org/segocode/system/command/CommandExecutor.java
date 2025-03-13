package org.segocode.system.command;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutor.class);
    private static final int MAX_RETRIES = 5;
    private static final int TIMEOUT = 120; // seconds

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

        // The -q option is important to prevent deadlocks by ensuring the output buffer is not filled.
        // TODO: make a StreamGobbler to handle buffered output and avoid deadlocks.
        String[] command = { ytDlpCommand, "-q", "-S", "ext,res:420", "-o", outputPath, url };

        LOGGER.debug("Starting download command: {}", String.join(" ", command));

        int attempt = 0;
        while (attempt++ < MAX_RETRIES) {
            LOGGER.info("Attempt {} of " + MAX_RETRIES, attempt);
            try {
                Process process = new ProcessBuilder(command).start();
                if (process.waitFor(TIMEOUT, TimeUnit.SECONDS) ? process.exitValue() == 0 : process.destroyForcibly() == null) {
                    LOGGER.info("Download successful for URL: {}", url);
                    return;
                } else {
                    LOGGER.warn("Download attempt {} failed or timed out.", attempt);
                    process.destroyForcibly();
                }
            } catch (IOException e) {
                LOGGER.error("I/O error occurred on attempt {} of " + MAX_RETRIES, attempt, e);
                if (attempt >= MAX_RETRIES) {
                    throw e;
                }
            }
        }
        LOGGER.error("Max retries reached. Command failed for URL: {}", url);
        throw new RuntimeException("Max retries reached. Command failed.");
    }
}
