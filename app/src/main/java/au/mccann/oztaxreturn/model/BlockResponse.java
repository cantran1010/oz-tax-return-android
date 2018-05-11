package au.mccann.oztaxreturn.model;

import java.io.Serializable;

/**
 * Created by LongBui on 6/15/17.
 */

public class BlockResponse implements Serializable {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BlockResponse{" +
                "status='" + status + '\'' +
                '}';
    }
}
