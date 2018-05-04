package au.mccann.oztaxreturn.fragment.basic;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.fragment.HomeFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.PersonalInformation;
import au.mccann.oztaxreturn.model.ResponseBasicInformation;
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

import static au.mccann.oztaxreturn.utils.TooltipUtils.showToolTipView;

/**
 * Created by CanTran on 4/19/18.
 */
public class SubmitInformation extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SubmitInformation.class.getSimpleName();
    private EdittextCustom edtBankName, edtBSB, edtAccountNumber, edtStreetName, edtSuburb, edtState, edtPostCode, edtPhone, edtEmail;
    private ButtonCustom btnSubmit;
    private ResponseBasicInformation basic;


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
        setTitle(getString(R.string.personal_information_title));
        appBarVisibility(false, true,0);
        basic = (ResponseBasicInformation) getArguments().getSerializable(Constants.KEY_BASIC_INFORMATION);
        LogUtils.d(TAG, "initData ResponseBasicInformation" + basic.toString());
        updateUI(basic);

    }

    private void updateUI(ResponseBasicInformation basic) {
        PersonalInformation pf = basic.getPersonalInformation();
        edtBankName.setText(pf.getBankAccountName());
        edtAccountNumber.setText(pf.getBankAccountNumber());
        edtBSB.setText(pf.getBankAccountBsb());
        edtStreetName.setText(pf.getStreet());
        edtSuburb.setText(pf.getSuburb());
        edtPhone.setText(pf.getPhone());
        edtEmail.setText(pf.getEmail());
    }


    @Override
    protected void resumeData() {


    }

    @Override
    public void onRefresh() {

    }

    private void doSaveBasic() {
        ProgressDialogUtils.showProgressDialog(getContext());
        JSONObject jsonRequest = new JSONObject();
        try {
            JSONObject salaryJson = new JSONObject();
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_BANK_NAME, edtBankName.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_BANK_NUMBER, edtAccountNumber.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_BANK_BSB, edtBSB.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_STREET, edtStreetName.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_SUBURB, edtSuburb.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_STATE, edtState.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_EMAIL, edtEmail.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_BASIC_INFO, salaryJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "doSaveBasic jsonRequest : " + jsonRequest.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().saveBasicInformation(UserManager.getUserToken(), basic.getAppId(), body).enqueue(new Callback<ResponseBasicInformation>() {
            @Override
            public void onResponse(Call<ResponseBasicInformation> call, Response<ResponseBasicInformation> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "doSaveBasic code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    openFragment(R.id.layout_container, HomeFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.e(TAG, "doSaveBasic error : " + error.message());
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
                LogUtils.e(TAG, "doSaveBasic onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getContext(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doSaveBasic();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
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
        if (!Utils.isValidEmail(edtEmail.getText().toString().trim())) {
            showToolTipView(getContext(), edtEmail, Gravity.TOP, getString(R.string.valid_app_email_2),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        doSaveBasic();
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
