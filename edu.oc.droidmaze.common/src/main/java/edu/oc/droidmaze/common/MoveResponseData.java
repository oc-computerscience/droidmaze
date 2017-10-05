package edu.oc.droidmaze.common;

import com.google.common.base.MoreObjects;
import edu.oc.droidmaze.api.MoveFailure;
import java.util.Objects;

public class MoveResponseData {

    private boolean success;
    private MoveFailure failureReason;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public MoveFailure getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(MoveFailure failureReason) {
        this.failureReason = failureReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MoveResponseData that = (MoveResponseData) o;
        return success == that.success &&
            failureReason == that.failureReason;
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, failureReason);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("success", success)
            .add("failureReason", failureReason)
            .toString();
    }
}
