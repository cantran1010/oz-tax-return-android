package au.mccann.oztaxreturn.activity;


import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CompoundButton;

import org.json.JSONException;
import org.json.JSONObject;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
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
import au.mccann.oztaxreturn.view.CheckBoxCustom;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.TextViewCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static au.mccann.oztaxreturn.utils.Utils.formatPhoneNumber;
import static au.mccann.oztaxreturn.utils.Utils.getCountryCode;
import static au.mccann.oztaxreturn.utils.Utils.openGeneralInfoActivity;
import static au.mccann.oztaxreturn.utils.Utils.showToolTip;

/**
 * Created by CanTran on 5/23/17.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EdittextCustom edtUsername, edtPassword, edtEmail, edtPhone, edtRePassword;
    private ButtonCustom btnRegister;

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
        CheckBoxCustom checkBox = findViewById(R.id.cb_policy);
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
        setUnderLinePolicy((TextViewCustom) findViewById(R.id.tv_policy));
    }

    @Override
    protected void resumeData() {

    }

    private void validateInput() {
        if (edtUsername.getText().toString().trim().isEmpty()) {
            edtUsername.requestFocus();
            showToolTip(this, edtUsername, getString(R.string.vali_all_empty));
        } else if (edtEmail.getText().toString().trim().isEmpty()) {
            edtEmail.requestFocus();
            showToolTip(this, edtEmail, getString(R.string.vali_all_empty));
        } else if (!Utils.isValidEmail(edtEmail.getText().toString().trim())) {
            edtEmail.requestFocus();
            showToolTip(this, edtEmail, getString(R.string.valid_app_email_2));
        } else if (!Utils.isValidPhone(edtPhone.getText().toString().trim())) {
            edtPhone.requestFocus();
            showToolTip(this, edtPhone, getString(R.string.valid_app_phone_2));
        } else if (edtPassword.getText().toString().trim().isEmpty()) {
            edtPassword.requestFocus();
            showToolTip(this, edtPassword, getString(R.string.vali_all_empty));
        } else if (edtPassword.getText().toString().trim().length() < 5) {
            edtPassword.requestFocus();
            showToolTip(this, edtPassword, getString(R.string.vali_password_lenth));
        } else if (!edtRePassword.getText().toString().equals(edtPassword.getText().toString())) {
            edtRePassword.requestFocus();
            showToolTip(this, edtRePassword, getString(R.string.vali_re_password));
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
            jsonRequest.put(Constants.PARAMETER_MOBILE, formatPhoneNumber(edtPhone.getText().toString().trim()));
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
                    clearData();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class), TransitionScreen.RIGHT_TO_LEFT);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.e(TAG, "doRegister onFailure : " + error.message());
                    switch (error.status()) {
                        case Constants.USERNAME_REQUIRE:
                        case Constants.USERNAME_UNIQUE:
                        case Constants.USERNAME_MAX:
                            edtUsername.requestFocus();
                            showToolTip(RegisterActivity.this, edtUsername, error.message());
                            break;
                        case Constants.PASSWORD_REQUIRE:
                        case Constants.PASSWORD_CONFIRM:
                            edtPassword.requestFocus();
                            showToolTip(RegisterActivity.this, edtPassword, error.message());
                            break;
                        case Constants.RE_PASSWORD_REQUIRE:
                            edtRePassword.requestFocus();
                            showToolTip(RegisterActivity.this, edtRePassword, error.message());
                        case Constants.EMAIL_REQUIRE:
                        case Constants.EMAIL_EMAIL:
                            edtEmail.requestFocus();
                            showToolTip(RegisterActivity.this, edtEmail, error.message());
                            break;
                        case Constants.PHONE_REQUIRED:
                        case Constants.PHONE_UNIQUE:
                            edtPhone.requestFocus();
                            showToolTip(RegisterActivity.this, edtPhone, error.message());
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
                                showToolTip(RegisterActivity.this, edtUsername, error.message());
                            } else if (error.status().startsWith("password")) {
                                edtPassword.requestFocus();
                                showToolTip(RegisterActivity.this, edtPassword, error.message());
                            } else if (error.status().startsWith("confirm_password")) {
                                edtRePassword.requestFocus();
                                showToolTip(RegisterActivity.this, edtRePassword, error.message());
                            } else if (error.status().startsWith("email")) {
                                edtEmail.requestFocus();
                                showToolTip(RegisterActivity.this, edtEmail, error.message());
                            } else if (error.status().startsWith("phone")) {
                                edtPhone.requestFocus();
                                showToolTip(RegisterActivity.this, edtPhone, error.message());
                            }
                            break;
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

    private void setUnderLinePolicy(TextViewCustom textViewCustom) {
        String text = getString(R.string.policy);
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);
        ClickableSpan conditionClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                openGeneralInfoActivity(RegisterActivity.this, getString(R.string.term), " http://oztax.tonishdev.com/terms-and-conditions");
            }
        };
        ClickableSpan nadClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                openGeneralInfoActivity(RegisterActivity.this, getString(R.string.register_privacy_policy), "http://oztax.tonishdev.com/privacy-policy");
            }
        };
        ssBuilder.setSpan(
                new ForegroundColorSpan(Color.parseColor("#577bb5")), // Span to add
                text.indexOf(getString(R.string.register_privacy_policy)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.register_privacy_policy)) + String.valueOf(getString(R.string.register_privacy_policy)).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        ssBuilder.setSpan(
                nadClickableSpan,
                text.indexOf(getString(R.string.register_privacy_policy)),
                text.indexOf(getString(R.string.register_privacy_policy)) + String.valueOf(getString(R.string.register_privacy_policy)).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        ssBuilder.setSpan(
                new ForegroundColorSpan(Color.parseColor("#577bb5")), // Span to add
                text.indexOf(getString(R.string.term)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.term)) + String.valueOf(getString(R.string.term)).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        ssBuilder.setSpan(
                conditionClickableSpan, // Span to add
                text.indexOf(getString(R.string.term)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.term)) + String.valueOf(getString(R.string.term)).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        textViewCustom.setText(ssBuilder);
        textViewCustom.setMovementMethod(LinkMovementMethod.getInstance());
        textViewCustom.setHighlightColor(Color.TRANSPARENT);
    }

    private void clearData() {
        edtUsername.setText("");
        edtEmail.setText("");
        edtPhone.setText("");
        edtRePassword.setText("");
        edtPassword.setText("");
        btnRegister.setText("");
        btnRegister.setText("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_register:
                LogUtils.d(TAG, "locale" + getCountryCode(this));
                validateInput();
                break;
        }
    }
}
