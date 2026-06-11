package org.segocode.webdl.system.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Deletes all files and folders within the 'downloads' folder.
     */
    public static void cleanDownloadsFolder() {
        Path downloadsFolder = Paths.get("./downloads/");
        try (Stream<Path> paths = Files.walk(downloadsFolder)) {
            paths.sorted(Comparator.reverseOrder())
                    .filter(p -> !p.equals(downloadsFolder))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                            LOGGER.info("Deleted: {}", p);
                        } catch (Exception e) {
                            LOGGER.error("Error deleting: {}", p, e);
                        }
                    });
        } catch (Exception e) {
            LOGGER.error("Error cleaning the downloads folder: {}", downloadsFolder, e);
        }
    }

    /**
     * Locates the video file, attempting to find or rename it with a .mp4 extension.
     *
     * @param filePath The path of the video file.
     * @return The video file if found or renamed successfully, null otherwise.
     */
    public static File locateVideoFile(String filePath) {
        File videoFile = new File(filePath);
        if (videoFile.exists()) return videoFile;

        // Attempt to locate the file without the .mp4 extension
        String filePathWithoutExtension = filePath.replace(".mp4", "");
        videoFile = new File(filePathWithoutExtension);
        if (videoFile.exists()) {
            File renamedFile = new File(filePathWithoutExtension + ".mp4");
            if (videoFile.renameTo(renamedFile)) return renamedFile;
        }
        return null;
    }
}
