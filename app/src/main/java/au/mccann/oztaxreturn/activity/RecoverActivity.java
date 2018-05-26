package au.mccann.oztaxreturn.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Spinner;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.adapter.OzSpinnerAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.CountryCodeResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
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
    private ArrayList<CountryCodeResponse> countryCodeResponses;
    private Spinner spCountryCode;

    @Override
    protected int getLayout() {
        return R.layout.activity_recover;
    }

    @Override
    protected void initView() {
        edtEmail = findViewById(R.id.edt_email);
        edtPhone = findViewById(R.id.edt_phone);
        ButtonCustom btnSend = findViewById(R.id.btn_send);
        spCountryCode = findViewById(R.id.sp_country_code);
        btnSend.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    edtPhone.setText("");
                }
            }
        });
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    edtEmail.setText("");
                }
            }
        });

    }

    @Override
    protected void resumeData() {
        getCountryCode();
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
            if (edtPhone.length() > 0) {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                try {
                    Phonenumber.PhoneNumber phone = phoneUtil.parse(edtPhone.getText().toString().trim(), countryCodeResponses.get(spCountryCode.getSelectedItemPosition()).getCode());
                    String result = phoneUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.E164);
                    jsonRequest.put(Constants.PARAMETER_MOBILE, result);
                    LogUtils.d(TAG, "doSaveBasic code : " + countryCodeResponses.get(spCountryCode.getSelectedItemPosition()).getCode());
                } catch (NumberParseException e) {
                    LogUtils.e(TAG, "doSaveBasic ERROR : " + e.toString());
                }
            }

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
                    DialogUtils.showOkDialog(RecoverActivity.this, getString(R.string.app_name), getString(R.string.send_successfully), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                        @Override
                        public void onSubmit() {
                            finish();
                        }
                    });

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

    private void getCountryCode() {
        ProgressDialogUtils.showProgressDialog(this);
        ApiClient.getApiService().getCountryCodeResponse(UserManager.getUserToken()).enqueue(new Callback<List<CountryCodeResponse>>() {
            @Override
            public void onResponse(Call<List<CountryCodeResponse>> call, Response<List<CountryCodeResponse>> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getCountryCode code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getCountryCode body : " + response.body().toString());
                    countryCodeResponses = (ArrayList<CountryCodeResponse>) response.body();

                    ArrayList<String> listCode = new ArrayList<>();
                    for (CountryCodeResponse countryCodeResponse : countryCodeResponses) {
                        listCode.add(countryCodeResponse.getDialCode());
                    }

                    OzSpinnerAdapter dataNameAdapter = new OzSpinnerAdapter(RecoverActivity.this, listCode, 1);
                    spCountryCode.setAdapter(dataNameAdapter);
//                    getBasicInformation();
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getCountryCode error : " + error.message());
                        DialogUtils.showOkDialog(RecoverActivity.this, getString(R.string.notification_title), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CountryCodeResponse>> call, Throwable t) {
                LogUtils.e(TAG, "getBasicInformation onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(RecoverActivity.this, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getCountryCode();
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
