package au.mccann.oztaxreturn.rest.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by LongBui on 5/3/18.
 */

public class ReviewFamilyHealthResponse {

    @SerializedName("dependants")
    private DependantsResponse dependantsResponse;

    @SerializedName("medicares")
    private MedicareResponse medicareResponse;

    @SerializedName("spouses")
    private ReviewSpouseResponse reviewSpouseResponse;

    private List<ReviewPrivateResponse> privates;

    public DependantsResponse getDependantsResponse() {
        return dependantsResponse;
    }

    public void setDependantsResponse(DependantsResponse dependantsResponse) {
        this.dependantsResponse = dependantsResponse;
    }

    public MedicareResponse getMedicareResponse() {
        return medicareResponse;
    }

    public void setMedicareResponse(MedicareResponse medicareResponse) {
        this.medicareResponse = medicareResponse;
    }

    public List<ReviewPrivateResponse> getPrivates() {
        return privates;
    }

    public void setPrivates(List<ReviewPrivateResponse> privates) {
        this.privates = privates;
    }

    public ReviewSpouseResponse getReviewSpouseResponse() {
        return reviewSpouseResponse;
    }

    public void setReviewSpouseResponse(ReviewSpouseResponse reviewSpouseResponse) {
        this.reviewSpouseResponse = reviewSpouseResponse;
    }

    @Override
    public String toString() {
        return "ReviewFamilyHealthResponse{" +
                "dependantsResponse=" + dependantsResponse +
                ", medicareResponse=" + medicareResponse +
                ", reviewSpouseResponse=" + reviewSpouseResponse +
                ", privates=" + privates +
                '}';
    }

}
