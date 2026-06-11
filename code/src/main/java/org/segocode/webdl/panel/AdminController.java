package org.segocode.webdl.panel;

import com.google.gson.Gson;
import io.javalin.http.Context;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.segocode.webdl.bot.model.DataRootContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    private final EmbeddedStorageManager storageManager;

    public AdminController(EmbeddedStorageManager storageManager) {
        this.storageManager = storageManager;
    }

    public void handleAdminRequest(Context ctx) {
        ctx.contentType("text/html");

        try {
            InputStream inputStream = getClass().getResourceAsStream("/views/admin.html");
            String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String jsonData;
            synchronized (storageManager) {
                jsonData = new Gson().toJson(((DataRootContainer) storageManager.root()).getUsers());
            }
            htmlContent = htmlContent.replace("{{!user_data}}", jsonData);

            ctx.result(htmlContent);
        } catch (IOException e) {
            LOGGER.error("Failed to read admin HTML file", e);
            ctx.status(500);
            ctx.result("Error loading admin panel: " + e.getMessage());
        }
    }
}
