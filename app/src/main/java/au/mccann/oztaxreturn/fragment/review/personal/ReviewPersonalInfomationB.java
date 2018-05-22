package au.mccann.oztaxreturn.fragment.review.personal;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.PersonalInfomationResponse;
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

import static au.mccann.oztaxreturn.utils.Utils.showToolTip;

/**
 * Created by LongBui on 4/23/18.
 */

public class ReviewPersonalInfomationB extends BaseFragment implements View.OnClickListener {

    private static final String TAG = ReviewPersonalInfomationB.class.getSimpleName();
    private EdittextCustom edtBankName, edtBSB, edtAccountNumber, edtStreetName, edtSuburb, edtState, edtPostCode, edtPhone, edtEmail;
    private ButtonCustom btnNext;
    private FloatingActionButton fab;
    private PersonalInfomationResponse personalInfomationResponse;


    @Override
    protected int getLayout() {
        return R.layout.review_personal_infomation_b_fragment;
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
        btnNext = (ButtonCustom) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if (isEditApp()) fab.setVisibility(View.VISIBLE);
        else fab.setVisibility(View.GONE);
        getReviewInformationB();
        getReviewProgress(getApplicationResponse());
    }

    @Override
    protected void resumeData() {
        appBarVisibility(true, true, 1);
    }

    @Override
    public void onRefresh() {

    }

    private void doEdit() {
        edtBankName.setEnabled(true);
        edtBSB.setEnabled(true);
        edtAccountNumber.setEnabled(true);
        edtStreetName.setEnabled(true);
        edtSuburb.setEnabled(true);
        edtState.setEnabled(true);
        edtPostCode.setEnabled(true);
        edtPhone.setEnabled(true);
        edtEmail.setEnabled(true);
    }

    private void updatePersonalInfo() {
        edtBankName.setText(personalInfomationResponse.getBankAccountName());
        edtBSB.setText(personalInfomationResponse.getBankAccountBsb());
        edtAccountNumber.setText(personalInfomationResponse.getBankAccountNumber());
        edtStreetName.setText(personalInfomationResponse.getStreet());
        edtSuburb.setText(personalInfomationResponse.getSuburb());
        edtState.setText(personalInfomationResponse.getState());
        edtPostCode.setText(personalInfomationResponse.getPostcode());
        edtPhone.setText(personalInfomationResponse.getPhone());
        edtEmail.setText(personalInfomationResponse.getEmail());
    }

    private void doNextB() {

        if (edtBankName.getText().toString().trim().isEmpty()) {
            showToolTip(getActivity(), edtBankName, getString(R.string.valid_app_bank_name));
            return;
        }
        if (edtBSB.getText().toString().trim().isEmpty()) {
            showToolTip(getActivity(), edtBSB, getString(R.string.valid_app_bsb));
            return;
        }
        if (edtAccountNumber.getText().toString().trim().isEmpty()) {
            showToolTip(getActivity(), edtAccountNumber, getString(R.string.valid_app_account_number));
            return;
        }
        if (edtStreetName.getText().toString().trim().isEmpty()) {
            showToolTip(getActivity(), edtStreetName, getString(R.string.valid_app_street_name));
            return;
        }
        if (edtSuburb.getText().toString().trim().isEmpty()) {
            showToolTip(getActivity(), edtSuburb, getString(R.string.valid_app_suburb));
            return;
        }
        if (edtState.getText().toString().trim().isEmpty()) {
            showToolTip(getActivity(), edtState, getString(R.string.valid_app_state));
            return;
        }
        if (edtPostCode.getText().toString().trim().isEmpty()) {
            showToolTip(getActivity(), edtPostCode, getString(R.string.valid_app_post_code));
            return;
        }
        if (edtPhone.getText().toString().trim().isEmpty()) {
            showToolTip(getActivity(), edtPhone, getString(R.string.valid_app_phone));
            return;
        }
        if (edtEmail.getText().toString().trim().isEmpty()) {
            showToolTip(getContext(), edtEmail, getString(R.string.valid_app_email));
            return;
        }

//        Utils.formatPhoneNumber(edtPhone.getText().toString().trim());


        ProgressDialogUtils.showProgressDialog(getActivity());
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("bank_account_name", edtBankName.getText().toString().trim());
            jsonRequest.put("bank_account_number", edtAccountNumber.getText().toString().trim());
            jsonRequest.put("bank_account_bsb", edtBSB.getText().toString().trim());
            jsonRequest.put("street", edtStreetName.getText().toString().trim());
            jsonRequest.put("suburb", edtSuburb.getText().toString().trim());
            jsonRequest.put("state", edtState.getText().toString().trim());
            jsonRequest.put("postcode", edtPostCode.getText().toString().trim());
            jsonRequest.put("phone", Utils.formatPhoneNumber(edtPhone.getText().toString().trim()));
            jsonRequest.put("email", edtEmail.getText().toString().trim());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtils.d(TAG, "doNextB jsonRequest : " + jsonRequest.toString());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().updatePersonalInfo(UserManager.getUserToken(), getApplicationResponse().getId(), body).enqueue(new Callback<PersonalInfomationResponse>() {
            @Override
            public void onResponse(Call<PersonalInfomationResponse> call, Response<PersonalInfomationResponse> response) {
                LogUtils.d(TAG, "doNextB code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doNextB body : " + response.body().toString());
//                    bundle.putSerializable(Constants.PERSONNAL_INFO_EXTRA, response.body());
                    openFragment(R.id.layout_container, ReviewPersonalInfomationC.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "doNextB error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }
                ProgressDialogUtils.dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<PersonalInfomationResponse> call, Throwable t) {
                LogUtils.e(TAG, "doNextB onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doNextB();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private void getReviewInformationB() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        ApiClient.getApiService().getReviewPersonalInfo(UserManager.getUserToken(), getApplicationResponse().getId()).enqueue(new Callback<PersonalInfomationResponse>() {
            @Override
            public void onResponse(Call<PersonalInfomationResponse> call, Response<PersonalInfomationResponse> response) {
                LogUtils.d(TAG, "getReviewInformationB code : " + response.code());

                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getReviewInformationB body : " + response.body().toString());
                    try {
                        personalInfomationResponse = response.body();
                        updatePersonalInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "getReviewInformationB error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

                ProgressDialogUtils.dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<PersonalInfomationResponse> call, Throwable t) {
                LogUtils.e(TAG, "getReviewInformationB onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getReviewInformationB();
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
            case R.id.fab:
                doEdit();
                break;

            case R.id.btn_next:
                if (isEditApp())
                    doNextB();
                else
                    openFragment(R.id.layout_container, ReviewPersonalInfomationC.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;
        }
    }
}
