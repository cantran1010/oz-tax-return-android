package au.mccann.oztaxreturn.networking;

import au.mccann.oztaxreturn.model.UserReponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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
}
