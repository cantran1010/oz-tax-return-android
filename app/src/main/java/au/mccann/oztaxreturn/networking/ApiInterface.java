package au.mccann.oztaxreturn.networking;

import au.mccann.oztaxreturn.model.RegisterReponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by CanTran on 5/23/17.
 */
public interface ApiInterface {
    @POST("auth/register")
    Call<RegisterReponse> register(@Body RequestBody body);

    @POST("auth/login")
    Call<RegisterReponse> login(@Body RequestBody body);
}
