package org.segocode.panel;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.segocode.bot.model.DataRootContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PanelController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PanelController.class);
    private final Javalin app;
    private final EmbeddedStorageManager storageManager;

    // Singleton instance
    private static PanelController instance;

    // Private constructor to prevent instantiation
    private PanelController(EmbeddedStorageManager storageManager) {
        this.storageManager = storageManager;
        this.app = Javalin.create().start(8080);

        // Configure routes
        configureRoutes();

        LOGGER.info("Panel controller started on port 8080");
    }

    public static synchronized PanelController start(EmbeddedStorageManager storageManager) {
        if (instance == null) {
            instance = new PanelController(storageManager);
        }
        return instance;
    }

    private void configureRoutes() {
        app.get("/", this::handleAdminRequest);
    }

private void handleAdminRequest(Context ctx) {
    ctx.contentType("text/html");

    try {
        Path htmlFilePath = Paths.get("src/main/java/org/segocode/panel/views/admin.html");
        String htmlContent = Files.readString(htmlFilePath);
        String jsonData = new Gson().toJson(((DataRootContainer)storageManager.root()).getUsers());
        htmlContent = htmlContent.replace("{{!user_data}}", jsonData);

        ctx.result(htmlContent);
    } catch (IOException e) {
        LOGGER.error("Failed to read admin HTML file", e);
        ctx.status(500);
        ctx.result("Error loading admin panel: " + e.getMessage());
    }
}
}