package edu.oc.droidmaze.common;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public final class LoginResponseData {

    private String token;

    public LoginResponseData() {}

    public LoginResponseData(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LoginResponseData that = (LoginResponseData) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("token", token)
            .toString();
    }
}
