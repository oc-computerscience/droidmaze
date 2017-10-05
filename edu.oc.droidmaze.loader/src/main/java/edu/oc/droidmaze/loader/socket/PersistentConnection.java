package edu.oc.droidmaze.loader.socket;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;

public abstract class PersistentConnection implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(PersistentConnection.class);

    final CountDownLatch latch = new CountDownLatch(1);

    private final String url;
    private final String title;

    public PersistentConnection(@NotNull final String url, @NotNull final String title) {
        this.url = url;
        this.title = title;
    }

    @Override
    public final void run() {
        final WebSocketClient client = new WebSocketClient();

        try {
            client.start();
            final URI socketUri = new URI(url.replace("http", "ws") + "/" + title);
            final ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(this, socketUri, request).get();
            latch.await();
        } catch (Exception e) {
            LOGGER.error("Error in WebSocket creation", e);
        } finally {
            try {
                client.stop();
            } catch (Exception e) {
                LOGGER.error("Error while stopping WebSocket client", e);
            }
        }
    }
}
