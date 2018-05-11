package au.mccann.oztaxreturn.activity;

import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserEntity;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.UserReponse;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.TextViewCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CanTran on 5/23/17.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EdittextCustom edtUsername, edtPassword;
    private TextViewCustom tvForgotPassword;

    @Override
    protected int getLayout() {
        return R.layout.activity_login;

    }

    @Override
    protected void initView() {
        edtUsername = findViewById(R.id.edt_user_name);
        edtPassword = findViewById(R.id.edt_password);
        tvForgotPassword = findViewById(R.id.tv_forgot_passwort);
        findViewById(R.id.btn_login).setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        underLineText(getString(R.string.forgot_password));

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void resumeData() {

    }

    private void underLineText(String mystring) {
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        tvForgotPassword.setText(content);
    }

    private void doLogin() {
        if (edtUsername.getText().toString().trim().isEmpty()) {
            edtUsername.requestFocus();
            edtUsername.setError(getString(R.string.vali_all_empty));
            return;
        }
        if (edtPassword.getText().toString().trim().isEmpty()) {
            edtPassword.requestFocus();
            edtPassword.setError(getString(R.string.vali_all_empty));
            return;
        }
        if (edtPassword.getText().toString().trim().length() < 5) {
            edtPassword.requestFocus();
            edtPassword.setError(getString(R.string.valid_password));
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
        LogUtils.d(TAG, "doLogin jsonRequest : " + jsonRequest.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().login(body).enqueue(new Callback<UserReponse>() {
            @Override
            public void onResponse(Call<UserReponse> call, Response<UserReponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "doLogin code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doLogin body : " + response.body().toString());
                    UserEntity user = response.body().getUser();
                    String tken = "Bearer " + response.body().getToken();
                    user.setToken(tken);
                    UserManager.insertUser(user);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class), TransitionScreen.RIGHT_TO_LEFT);
                    sendRegistrationToServer();
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
            public void onFailure(Call<UserReponse> call, Throwable t) {
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

    private void sendRegistrationToServer() {
        if (UserManager.checkLogin()) {
            final JSONObject jsonRequest = new JSONObject();
            try {
                jsonRequest.put("token", FirebaseInstanceId.getInstance().getToken());
                jsonRequest.put("type", "android");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            LogUtils.d(TAG, "sendRegistrationToServer , FirebaseInstanceId token : " + FirebaseInstanceId.getInstance().getToken());
            LogUtils.d(TAG, "sendRegistrationToServer , jsonRequest : " + jsonRequest.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
            ApiClient.getApiService().updatePushToken(UserManager.getUserToken(), body).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    LogUtils.d(TAG, "sendRegistrationToServer , code : " + response.code());

                    if (response.code() == Constants.HTTP_CODE_NO_CONTENT) {
                        LogUtils.d(TAG, "sendRegistrationToServer , body : " + response.body());

                    } else {
                        APIError error = Utils.parseError(response);
                        LogUtils.d(TAG, "sendRegistrationToServer error : " + error.message());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    LogUtils.e(TAG, "sendRegistrationToServer , onFailure : " + t.getMessage());
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                doLogin();
                break;
            case R.id.tv_forgot_passwort:
                startActivity(new Intent(LoginActivity.this, RecoverActivity.class), TransitionScreen.RIGHT_TO_LEFT);
                break;

        }
    }
}
