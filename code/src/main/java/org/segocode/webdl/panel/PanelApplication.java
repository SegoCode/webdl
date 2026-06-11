package org.segocode.webdl.panel;

import io.javalin.Javalin;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PanelApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(PanelApplication.class);
    private static PanelApplication instance;

    private PanelApplication(EmbeddedStorageManager storageManager) {
        AdminController adminController = new AdminController(storageManager);
        Javalin app = Javalin.create().start(8080);
        configureRoutes(app, adminController);
        LOGGER.info("Panel application started on port 8080");
    }

    public static synchronized void start(EmbeddedStorageManager storageManager) {
        if (instance == null) {
            instance = new PanelApplication(storageManager);
        }
    }

    private void configureRoutes(Javalin app, AdminController adminController) {
        app.get("/", adminController::handleAdminRequest);
    }
}
