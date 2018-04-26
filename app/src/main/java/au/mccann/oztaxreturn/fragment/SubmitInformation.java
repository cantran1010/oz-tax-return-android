package au.mccann.oztaxreturn.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.ResponseBasicInformation;
import au.mccann.oztaxreturn.networking.ApiClient;
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

import static au.mccann.oztaxreturn.utils.TooltipUtils.showToolTipView;

/**
 * Created by CanTran on 4/19/18.
 */
public class SubmitInformation extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SubmitInformation.class.getSimpleName();
    private EdittextCustom edtBankName, edtBSB, edtAccountNumber, edtStreetName, edtSuburb, edtState, edtPostCode, edtPhone, edtEmail;
    private ButtonCustom btnSubmit;
    private Bundle bundle = new Bundle();

    @Override
    protected int getLayout() {
        return R.layout.fragment_personal_submit;
    }

    @Override
    protected void initView() {
        edtBankName = (EdittextCustom) findViewById(R.id.edt_bank_account);
        edtBSB = (EdittextCustom) findViewById(R.id.edt_bsb);
        edtAccountNumber = (EdittextCustom) findViewById(R.id.edt_account_number);
        edtStreetName = (EdittextCustom) findViewById(R.id.edt_street_name);
        edtSuburb = (EdittextCustom) findViewById(R.id.edt_suburb);
        edtState = (EdittextCustom) findViewById(R.id.edt_state);
        edtPostCode = (EdittextCustom) findViewById(R.id.edt_post_code);
        edtPhone = (EdittextCustom) findViewById(R.id.edt_phone);
        edtEmail = (EdittextCustom) findViewById(R.id.edt_email);
        btnSubmit = (ButtonCustom) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        bundle = getArguments();
        LogUtils.d(TAG, "initData bundle : " + bundle.toString());
        setTitle(getString(R.string.personal_information_title));
        appBarVisibility(false, true,0);

    }

    @Override
    protected void resumeData() {


    }

    @Override
    public void onRefresh() {

    }

    private void doSubmit() {
        if (edtBankName.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtBankName, Gravity.BOTTOM, getString(R.string.valid_app_bank_name),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtBSB.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtBSB, Gravity.BOTTOM, getString(R.string.valid_app_bsb),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtAccountNumber.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtAccountNumber, Gravity.TOP, getString(R.string.valid_app_account_number),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtStreetName.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtStreetName, Gravity.TOP, getString(R.string.valid_app_street_name),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtSuburb.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtSuburb, Gravity.TOP, getString(R.string.valid_app_suburb),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtState.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtState, Gravity.TOP, getString(R.string.valid_app_state),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtPostCode.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtPostCode, Gravity.TOP, getString(R.string.valid_app_post_code),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtPhone.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtPhone, Gravity.TOP, getString(R.string.valid_app_phone),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtEmail.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtEmail, Gravity.TOP, getString(R.string.valid_app_email),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        saveBasicInformation();
    }

    private void saveBasicInformation() {
        ProgressDialogUtils.showProgressDialog(getContext());
        JSONObject jsonRequest = new JSONObject();
        try {
            if (bundle.getIntArray(Constants.PARAMETER_WAGE_ATTACHMENTS) != null) {
                JSONArray jsonArray = new JSONArray();
                for (int anImagesArr : bundle.getIntArray(Constants.PARAMETER_WAGE_ATTACHMENTS))
                    jsonArray.put(anImagesArr);
                jsonRequest.put(Constants.PARAMETER_WAGE_ATTACHMENTS, jsonArray);
            }
            if (bundle.getIntArray(Constants.PARAMETER_INCOME_CONTENT_ATTACHMENTS) != null) {
                JSONArray jsonArray = new JSONArray();
                for (int anImagesArr : bundle.getIntArray(Constants.PARAMETER_INCOME_CONTENT_ATTACHMENTS))
                    jsonArray.put(anImagesArr);
                jsonRequest.put(Constants.PARAMETER_INCOME_CONTENT_ATTACHMENTS, jsonArray);
            }
            if (bundle.getIntArray(Constants.PARAMETER_DEDUCTION_ATTACHMENTS) != null) {
                JSONArray jsonArray = new JSONArray();
                for (int anImagesArr : bundle.getIntArray(Constants.PARAMETER_DEDUCTION_ATTACHMENTS))
                    jsonArray.put(anImagesArr);
                jsonRequest.put(Constants.PARAMETER_DEDUCTION_ATTACHMENTS, jsonArray);
            }
            jsonRequest.put(Constants.PARAMETER_INCOME_TFN, bundle.getString(Constants.PARAMETER_INCOME_TFN));
            jsonRequest.put(Constants.PARAMETER_INCOME_FIRST_NAME, bundle.getString(Constants.PARAMETER_INCOME_FIRST_NAME));
            jsonRequest.put(Constants.PARAMETER_INCOME_MID_NAME, bundle.getString(Constants.PARAMETER_INCOME_MID_NAME));
            jsonRequest.put(Constants.PARAMETER_INCOME_LAST_NAME, bundle.getString(Constants.PARAMETER_INCOME_LAST_NAME));
            jsonRequest.put(Constants.PARAMETER_INCOME_BIRTH_DAY, bundle.getString(Constants.PARAMETER_INCOME_BIRTH_DAY));
            jsonRequest.put(Constants.PARAMETER_INCOME_CONTENT, bundle.getString(Constants.PARAMETER_INCOME_CONTENT));
            jsonRequest.put(Constants.PARAMETER_DEDUCTION_CONTENT, bundle.getString(Constants.PARAMETER_DEDUCTION_CONTENT));
            jsonRequest.put(Constants.PARAMETER_APP_TITLE, bundle.getString(Constants.PARAMETER_APP_TITLE));
            jsonRequest.put(Constants.PARAMETER_APP_FIRST_NAME, bundle.getString(Constants.PARAMETER_APP_FIRST_NAME));
            jsonRequest.put(Constants.PARAMETER_APP_MID_NAME, bundle.getString(Constants.PARAMETER_APP_MID_NAME));
            jsonRequest.put(Constants.PARAMETER_APP_LAST_NAME, bundle.getString(Constants.PARAMETER_APP_LAST_NAME));
            jsonRequest.put(Constants.PARAMETER_APP_BIRTH_DAY, bundle.getString(Constants.PARAMETER_APP_BIRTH_DAY));
            jsonRequest.put(Constants.PARAMETER_APP_GENDER, bundle.getString(Constants.PARAMETER_APP_GENDER));
            jsonRequest.put(Constants.PARAMETER_APP_GENDER, bundle.getString(Constants.PARAMETER_APP_GENDER));
            jsonRequest.put(Constants.PARAMETER_APP_LOCAL, bundle.getBoolean(Constants.PARAMETER_APP_LOCAL));
            jsonRequest.put(Constants.PARAMETER_APP_BANK_ACCOUNT_NAME, edtBankName.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_APP_BANK_ACCOUNT_NUMBER, edtAccountNumber.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_APP_BANK_ACCOUNT_BSB, edtBSB.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_APP_STREET, edtStreetName.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_APP_SUBURB, edtSuburb.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_APP_STATE, edtState.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_APP_POST_CODE, edtPostCode.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_APP_PHONE, edtPhone.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_APP_EMAIL, edtEmail.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "saveBasicInformation jsonRequest : " + jsonRequest.toString());
        LogUtils.d(TAG, "saveBasicInformation PARAMETER_APP_ID : " + bundle.getInt(Constants.PARAMETER_APP_ID));
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().saveBasicInformation(UserManager.getUserToken(), bundle.getInt(Constants.PARAMETER_APP_ID), body).enqueue(new Callback<ResponseBasicInformation>() {
            @Override
            public void onResponse(Call<ResponseBasicInformation> call, Response<ResponseBasicInformation> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "saveBasicInformation code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_NO_CONTENT) {
                    Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.e(TAG, "saveBasicInformation onFailure : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(getContext(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBasicInformation> call, Throwable t) {
                LogUtils.e(TAG, "saveBasicInformation onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getContext(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        saveBasicInformation();
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
            case R.id.btn_submit:
                doSubmit();
                break;

        }

    }


}
