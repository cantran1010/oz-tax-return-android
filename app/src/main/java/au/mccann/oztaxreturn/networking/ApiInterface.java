package au.mccann.oztaxreturn.networking;

import java.util.List;

import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.DeductionResponse;
import au.mccann.oztaxreturn.model.IncomeResponse;
import au.mccann.oztaxreturn.model.ResponseBasicInformation;
import au.mccann.oztaxreturn.model.Summary;
import au.mccann.oztaxreturn.model.UserReponse;
import au.mccann.oztaxreturn.rest.response.ApplicationResponse;
import au.mccann.oztaxreturn.rest.response.FeeResponse;
import au.mccann.oztaxreturn.rest.response.PersonalInfomationResponse;
import au.mccann.oztaxreturn.rest.response.ReviewFamilyHealthResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by CanTran on 5/23/17.
 */
public interface ApiInterface {
    @POST("auth/register")
    Call<UserReponse> register(@Body RequestBody body);

    @POST("auth/login")
    Call<UserReponse> login(@Body RequestBody body);

    @POST("auth/recover")
    Call<Void> recoverPasswor(@Body RequestBody body);

    @POST("application")
    Call<ApplicationResponse> createApplication(@Header("Authorization") String token, @Body RequestBody body);

    @POST("application/duplicate")
    Call<ApplicationResponse> duplicateApplication(@Header("Authorization") String token, @Body RequestBody body);

    @GET("application/list")
    Call<List<ApplicationResponse>> getAllApplication(@Header("Authorization") String token);

    @Multipart
    @POST("attachment")
    Call<List<Attachment>> uploadImage(@Header("Authorization") String token, @Part List<MultipartBody.Part> files);

    @DELETE("application/{application_id}")
    Call<Void> deleteApplication(@Header("Authorization") String token, @Path("application_id") int appId);

    @GET("application/{application_id}/basic_info")
    Call<ResponseBasicInformation> getBasicInformation(@Header("Authorization") String token, @Path("application_id") int appId);

    @PUT("application/{application_id}/basic_info")
    Call<ResponseBasicInformation> saveBasicInformation(@Header("Authorization") String token, @Path("application_id") int appId, @Body RequestBody body);

    @GET("application/{application_id}/review/personal_info")
    Call<PersonalInfomationResponse> getReviewPersonalInfo(@Header("Authorization") String token, @Path("application_id") int appId);

    @GET("application/{application_id}/review/income")
    Call<IncomeResponse> getReviewIncome(@Header("Authorization") String token, @Path("application_id") int appId);

    @PUT("application/{application_id}/review/income")
    Call<IncomeResponse> putReviewIncom(@Header("Authorization") String token, @Path("application_id") int appId, @Body RequestBody body);

    @PUT("application/{application_id}/review/personal_info")
    Call<PersonalInfomationResponse> updatePersonalInfo(@Header("Authorization") String token, @Path("application_id") int appId, @Body RequestBody body);

    @GET("application/{application_id}/fees")
    Call<FeeResponse> getPromotionFee(@Header("Authorization") String token, @Path("application_id") int appId);

    @POST("application/{application_id}/promotion")
    Call<FeeResponse> checkPromotionCode(@Header("Authorization") String token, @Path("application_id") int appId, @Body RequestBody body);

    @POST("application/{application_id}/checkout")
    Call<Void> checkout(@Header("Authorization") String token, @Path("application_id") int appId, @Body RequestBody body);

    @GET("application/{application_id}/review/deduction")
    Call<DeductionResponse> getReviewDeduction(@Header("Authorization") String token, @Path("application_id") int appId);

    @PUT("application/{application_id}/review/deduction")
    Call<DeductionResponse> putReviewDeduction(@Header("Authorization") String token, @Path("application_id") int appId, @Body RequestBody body);

    @GET("application/{application_id}/review/health")
    Call<ReviewFamilyHealthResponse> getReviewFamilyHealth(@Header("Authorization") String token, @Path("application_id") int appId);

    @PUT("application/{application_id}/review/health")
    Call<ReviewFamilyHealthResponse> updateReviewFamilyHealth(@Header("Authorization") String token, @Path("application_id") int appId, @Body RequestBody body);

    @GET("application/{application_id}/review/summary")
    Call<Summary> getReviewSummary(@Header("Authorization") String token, @Path("application_id") int appId);

    @PUT("application/{application_id}/review/lodge")
    Call<ApplicationResponse> loggeApplicaction(@Header("Authorization") String token, @Path("application_id") int appId);
}
