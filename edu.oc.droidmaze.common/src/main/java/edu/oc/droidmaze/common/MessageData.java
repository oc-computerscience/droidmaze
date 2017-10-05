package edu.oc.droidmaze.common;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import java.util.UUID;

public final class MessageData {

    private UUID uuid;
    private String message;

    public MessageData() {}

    public MessageData(UUID uuid, String message) {
        this.uuid = uuid;
        this.message = message;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MessageData that = (MessageData) o;
        return Objects.equals(uuid, that.uuid) &&
            Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, message);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("uuid", uuid)
            .add("message", message)
            .toString();
    }
}
