package ari.superarilo.tool;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CustomLogger  {
    private final Logger logger = Logger.getLogger("Ari");

    public CustomLogger(Boolean debug) {
        this.logger.setLevel(debug ? Level.FINER:Level.INFO);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(debug ? Level.FINER:Level.INFO);
        handler.setFormatter(new SimpleFormatter());
        this.logger.addHandler(handler);
        this.logger.setUseParentHandlers(false);
    }

    public Logger getLogger() {
        return logger;
    }
}
