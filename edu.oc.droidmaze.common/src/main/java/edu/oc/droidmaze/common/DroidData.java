package edu.oc.droidmaze.common;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class DroidData {

    private UUID id;
    private String name;

    public DroidData() {}

    public DroidData(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(@NotNull UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DroidData droidData = (DroidData) o;
        return Objects.equals(id, droidData.id) &&
            Objects.equals(name, droidData.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("name", name)
            .toString();
    }
}
