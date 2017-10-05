package edu.oc.droidmaze.loader.config;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public final class Config {

    private String serverUrl;

    public String getServerUrl() {
        return serverUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Config config = (Config) o;
        return Objects.equals(serverUrl, config.serverUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverUrl);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("serverUrl", serverUrl)
            .toString();
    }
}
