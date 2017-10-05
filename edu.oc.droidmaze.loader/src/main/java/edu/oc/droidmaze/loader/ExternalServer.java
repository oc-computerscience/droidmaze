package edu.oc.droidmaze.loader;

import com.google.common.collect.ImmutableSet;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import edu.oc.droidmaze.api.Droid;
import edu.oc.droidmaze.common.DroidData;
import edu.oc.droidmaze.common.LoginResponseData;
import edu.oc.droidmaze.common.MoveData;
import edu.oc.droidmaze.common.MoveResponseData;
import edu.oc.droidmaze.common.ServerData;
import edu.oc.droidmaze.loader.impl.LoaderMaze;
import edu.oc.droidmaze.loader.impl.LoaderServer;
import edu.oc.droidmaze.loader.impl.VirtualDroid;
import edu.oc.droidmaze.loader.socket.Messenger;
import edu.oc.droidmaze.loader.socket.Overwatch;
import edu.oc.droidmaze.loader.util.Util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ExternalServer {

    private static final Logger LOGGER = LogManager.getLogger(ExternalServer.class);

    private static ExternalServer instance;

    private final List<Method> failureMethods = new ArrayList<>();

    private final String url;
    private String token;

    private Overwatch overwatch;

    private Messenger messenger;

    static {
        Unirest.setObjectMapper(new ObjectMapper() {
            @Override
            public <T> T readValue(String value, Class<T> valueType) {
                return Util.gson().fromJson(value, valueType);
            }

            @Override
            public String writeValue(Object value) {
                return Util.gson().toJson(value);
            }
        });
    }

    @NotNull
    @Contract(pure = true)
    public static ExternalServer getInstance() {
        return instance;
    }

    public ExternalServer(@NotNull final String url) {
        this.url = url;
        instance = this;
    }

    public void registerFailureHandlerMethods(@NotNull final List<Method> failureMethods) {
        this.failureMethods.addAll(failureMethods);
    }

    public void init() {
        // Don't cancel when debugging
        if (isTestMode()) {
            Util.CHECK_INTERRUPT = false;
        }

        acquireToken();
    }

    public void updateState() {
        try {
            final HttpResponse<ServerData> resp = Unirest.get(url + "/mazestate")
                .header("accept", "application/json")
                .queryString("token", token)
                .asObject(ServerData.class);

            updateLoaderState(resp.getBody());
        } catch (final UnirestException e) {
            // TODO: uuh.... retry actually
            LOGGER.error("Failed to get maze state, retrying...", e);
            System.exit(1);
        }
    }

    private void updateLoaderState(final ServerData server) {
        final LoaderServer loaderServer = LoaderServer.getInstance();

        loaderServer.setTurnCount(server.getTurnCount());

        final Set<DroidData> droids = server.getDroids();
        loaderServer.setDroids(ImmutableSet.<Droid>builder()
            .addAll(droids.stream().map(VirtualDroid::parse).collect(Collectors.toList()))
            .build());

        loaderServer.setMaze(LoaderMaze.parse(server.getMaze()));
    }

    public void pushNewState() {
        final MoveData data = LoaderServer.getInstance().getNextMove();
        if (data == null) {
            return;
        }

        HttpResponse<MoveResponseData> resp = null;
        try {
            resp = Unirest.post(url + "/move")
                .header("accept", "application/json")
                .queryString("token", token)
                .body(data)
                .asObject(MoveResponseData.class);
        } catch (final UnirestException e) {
            LOGGER.error("No response on state push", e);
            // We can't continue if we don't get a response back
            System.exit(1);
        }

        if (resp == null) {
            LOGGER.error("Error getting response from state push");
            System.exit(1);
        }

        final MoveResponseData body = resp.getBody();
        if (!body.isSuccess()) {
            final Droid droid = LoaderServer.getInstance().getUserDroid();

            for (Method failureMethod : failureMethods) {
                try {
                    if (failureMethod.getParameters().length == 0) {
                        failureMethod.invoke(droid);
                    } else {
                        failureMethod.invoke(droid, body.getFailureReason());
                    }
                } catch (final IllegalAccessException | InvocationTargetException e) {
                    LOGGER.error("Exception in failure handler", e.getCause());
                }
            }
        }
    }

    private boolean isTestMode() {
        try {
            return Unirest.get(url + "/test")
                .header("accept", "application/json")
                .asJson().getBody().getObject().getBoolean("testMode");
        } catch (final UnirestException e) {
            LOGGER.warn("Error occurred asking the server if this is test mode", e);
            return false;
        }
    }

    @NotNull
    @Contract(pure = true)
    public String getToken() {
        return token;
    }

    private void acquireToken() {
        try {
            final Droid droid = LoaderServer.getInstance().getUserDroid();

            final HttpResponse<LoginResponseData> resp = Unirest.post(url + "/login")
                .header("accept", "application/json")
                .body(new DroidData(droid.getId(), droid.getName()))
                .asObject(LoginResponseData.class);

            token = resp.getBody().getToken();
        } catch (final UnirestException e) {
            LOGGER.error("Failed getting token", e);
            // We can't continue without a token
            System.exit(1);
        }
    }

    public Overwatch getOverwatch() {
        return overwatch;
    }

    public void setOverwatch(Overwatch overwatch) {
        this.overwatch = overwatch;
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public void setMessenger(Messenger messenger) {
        this.messenger = messenger;
    }
}
