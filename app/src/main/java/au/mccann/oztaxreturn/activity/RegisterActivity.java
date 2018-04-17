package au.mccann.oztaxreturn.activity;


import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

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
import au.mccann.oztaxreturn.view.ButtonCustom;
import au.mccann.oztaxreturn.view.EdittextCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CanTran on 5/23/17.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EdittextCustom edtUsername, edtPassword, edtEmail, edtPhone, edtRePassword;
    private ButtonCustom btnRegister;
    private CheckBox checkBox;

    @Override
    protected int getLayout() {
        return R.layout.activity_register;

    }

    @Override
    protected void initView() {
        findViewById(R.id.img_back).setOnClickListener(this);
        edtUsername = findViewById(R.id.edt_user_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPhone = findViewById(R.id.edt_mobile);
        edtRePassword = findViewById(R.id.edt_re_password);
        edtPassword = findViewById(R.id.edt_password);
        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
        checkBox = findViewById(R.id.cb_policy);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    btnRegister.setEnabled(true);
                else btnRegister.setEnabled(false);
            }
        });

    }

    @Override
    protected void initData() {
    }

    @Override
    protected void resumeData() {

    }

    private void validateInput() {
        if (edtUsername.getText().toString().trim().isEmpty()) {
            edtUsername.requestFocus();
            edtUsername.setError("");
        } else if (!Utils.isValidEmail(edtEmail.getText().toString().trim())) {
            edtEmail.requestFocus();
            edtEmail.setError("");
        } else if (!Utils.isValidPhone(edtPhone.getText().toString().trim())) {
            edtPhone.requestFocus();
            edtPhone.setError("");
        } else if (edtPassword.getText().toString().trim().length() < 6) {
            edtPassword.requestFocus();
            edtPassword.setError("");
        } else if (edtRePassword.getText().toString().trim().length() < 6) {
            edtRePassword.requestFocus();
            edtRePassword.setError("");
        } else if (!edtRePassword.getText().toString().equals(edtPassword.getText().toString())) {
            edtRePassword.requestFocus();
            edtRePassword.setError("");
        } else {
            doRegister();
        }
    }

    private void doRegister() {
        ProgressDialogUtils.showProgressDialog(RegisterActivity.this);
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put(Constants.PARAMETER_USERNAME, edtUsername.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_EMAIL, edtEmail.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_MOBILE, edtPhone.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_PASSWORD, edtPassword.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_RE_PASSWORD, edtRePassword.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "doRegister jsonRequest : " + jsonRequest.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().register(body).enqueue(new Callback<UserReponse>() {
            @Override
            public void onResponse(Call<UserReponse> call, Response<UserReponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "doRegister code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doRegister body: " + response.body().toString());
                    UserEntity user = response.body().getUser();
                    user.setToken(response.body().getToken());
                    UserManager.insertUser(user);
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class), TransitionScreen.RIGHT_TO_LEFT);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.e(TAG, "doRegister onFailure : " + error.message());
                    if (error != null) switch (error.status()) {
                        case Constants.USERNAME_REQUIRE:
                        case Constants.USERNAME_UNIQUE:
                        case Constants.USERNAME_MAX:
                            edtUsername.requestFocus();
                            edtUsername.setError(error.message());
                            break;
                        case Constants.PASSWORD_REQUIRE:
                        case Constants.PASSWORD_CONFIRM:
                            edtPassword.requestFocus();
                            edtPassword.setError(error.message());
                            break;
                        case Constants.RE_PASSWORD_REQUIRE:
                            edtRePassword.requestFocus();
                            edtRePassword.setError(error.message());
                        case Constants.EMAIL_REQUIRE:
                        case Constants.EMAIL_EMAIL:
                            edtEmail.requestFocus();
                            edtEmail.setError(error.message());
                            break;
                        case Constants.PHONE_REQUIRED:
                        case Constants.PHONE_UNIQUE:
                            edtPhone.requestFocus();
                            edtPhone.setError(error.message());
                            break;
                        case Constants.SYSTEM_ERROR:
                            DialogUtils.showOkDialog(RegisterActivity.this, getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                                @Override
                                public void onSubmit() {

                                }
                            });
                        default:
                            if (error.status().startsWith("username")) {
                                edtUsername.requestFocus();
                                edtUsername.setError(error.message());
                            } else if (error.status().startsWith("password")) {
                                edtPassword.requestFocus();
                                edtPassword.setError(error.message());
                            } else if (error.status().startsWith("email")) {
                                edtEmail.requestFocus();
                                edtEmail.setError(error.message());

                            } else if (error.status().startsWith("phone")) {
                                edtPhone.requestFocus();
                                edtPhone.setError(error.message());
                            }
                            break;
                    }
                    else {
                        DialogUtils.showOkDialog(RegisterActivity.this, getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<UserReponse> call, Throwable t) {
                LogUtils.e(TAG, "doRegister onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(RegisterActivity.this, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doRegister();
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
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_register:
                validateInput();
                break;
        }
    }
}
