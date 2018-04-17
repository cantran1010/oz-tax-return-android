package au.mccann.oztaxreturn.activity;

import android.content.Intent;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserEntity;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.RegisterReponse;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.EdittextCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CanTran on 5/23/17.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EdittextCustom edtUsername, edtPassword;

    @Override
    protected int getLayout() {
        return R.layout.activity_login;

    }

    @Override
    protected void initView() {
        edtUsername = findViewById(R.id.edt_user_name);
        edtPassword = findViewById(R.id.edt_password);
        findViewById(R.id.btn_login).setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void resumeData() {

    }

    private void doLogin() {
        if (edtUsername.getText().toString().trim().isEmpty()) {
            edtUsername.requestFocus();
            edtUsername.setError("");
            return;
        }
        if (edtPassword.getText().toString().trim().isEmpty()) {
            edtPassword.requestFocus();
            edtPassword.setError("");
            return;
        }
        if (edtPassword.getText().toString().trim().length() < 6) {
            edtPassword.requestFocus();
            edtPassword.setError("");
            return;
        }
        ProgressDialogUtils.showProgressDialog(LoginActivity.this);
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put(Constants.PARAMETER_USERNAME, edtUsername.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_PASSWORD, edtPassword.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().login(body).enqueue(new Callback<RegisterReponse>() {
            @Override
            public void onResponse(Call<RegisterReponse> call, Response<RegisterReponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "doLogin code" + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doLogin body" + response.body().toString());
                    UserEntity user = response.body().getUser();
                    user.setToken(response.body().getToken());
                    UserManager.insertUser(user);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class), TransitionScreen.FADE_IN);
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        DialogUtils.showOkDialog(LoginActivity.this, getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });


                    }
                }

            }

            @Override
            public void onFailure(Call<RegisterReponse> call, Throwable t) {
                LogUtils.e(TAG, "doLogin onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(LoginActivity.this, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doLogin();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                doLogin();
                break;

        }
    }
}
