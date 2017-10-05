package edu.oc.droidmaze.loader.config;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public class DroidConfig {

    private String moduleName;

    public String getModuleName() {
        return moduleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DroidConfig that = (DroidConfig) o;
        return Objects.equals(moduleName, that.moduleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleName);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("moduleName", moduleName)
            .toString();
    }
}
