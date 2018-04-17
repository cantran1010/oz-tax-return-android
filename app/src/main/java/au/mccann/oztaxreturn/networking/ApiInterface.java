package au.mccann.oztaxreturn.networking;

import au.mccann.oztaxreturn.model.RegisterReponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;

/**
 * Created by LongBui on 09/05/2017.
 */
public interface ApiInterface {
    @PUT("auth/register")
    Call<RegisterReponse> register(@Body RequestBody body);


}
