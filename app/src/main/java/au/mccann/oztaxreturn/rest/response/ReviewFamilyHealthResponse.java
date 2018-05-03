package au.mccann.oztaxreturn.rest.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LongBui on 5/3/18.
 */

public class ReviewFamilyHealthResponse {

    @SerializedName("dependants")
    private DependantsResponse dependantsResponse;

    public DependantsResponse getDependantsResponse() {
        return dependantsResponse;
    }

    public void setDependantsResponse(DependantsResponse dependantsResponse) {
        this.dependantsResponse = dependantsResponse;
    }

    @Override
    public String toString() {
        return "ReviewFamilyHealthResponse{" +
                "dependantsResponse=" + dependantsResponse +
                '}';
    }

}
