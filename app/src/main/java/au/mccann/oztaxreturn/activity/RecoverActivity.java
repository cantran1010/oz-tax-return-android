package au.mccann.oztaxreturn.activity;

import android.content.Intent;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.model.APIError;
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

public class RecoverActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EdittextCustom edtEmail, edtPhone;
    private ButtonCustom btnSend;

    @Override
    protected int getLayout() {
        return R.layout.activity_recover;
    }

    @Override
    protected void initView() {
        edtEmail = findViewById(R.id.edt_email);
        edtPhone = findViewById(R.id.edt_phone);
        btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void resumeData() {

    }

    private void doRecover() {
        if (edtEmail.getText().toString().trim().isEmpty() && edtPhone.getText().toString().trim().isEmpty()) {
            DialogUtils.showOkDialog(RecoverActivity.this, getString(R.string.error), getString(R.string.error_empty), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                @Override
                public void onSubmit() {

                }
            });
            return;
        }


        ProgressDialogUtils.showProgressDialog(RecoverActivity.this);
        JSONObject jsonRequest = new JSONObject();
        try {
            if (edtEmail.length() > 0)
                jsonRequest.put(Constants.PARAMETER_EMAIL, edtEmail.getText().toString().trim());
            if (edtPhone.length() > 0)
                jsonRequest.put(Constants.PARAMETER_MOBILE, edtPhone.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().recoverPasswor(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "doRecover code" + response.code());
                if (response.code() == Constants.HTTP_CODE_NO_CONTENT) {
                    LogUtils.d(TAG, "doRecover body" + response.body().toString());
                    startActivity(new Intent(RecoverActivity.this, HomeActivity.class), TransitionScreen.RIGHT_TO_LEFT);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "doRecover error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(RecoverActivity.this, getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LogUtils.e(TAG, "doRecover onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(RecoverActivity.this, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doRecover();
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
            case R.id.btn_send:
                doRecover();
                break;

        }

    }
}
