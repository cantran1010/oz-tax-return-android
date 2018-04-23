package au.mccann.oztaxreturn.fragment.review.personal;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

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
import au.mccann.oztaxreturn.utils.TooltipUtils;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.ButtonCustom;
import au.mccann.oztaxreturn.view.EdittextCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 4/23/18.
 */

public class ReviewPersonalInfomationB extends BaseFragment implements View.OnClickListener {

    private static final String TAG = ReviewPersonalInfomationB.class.getSimpleName();
    private EdittextCustom edtBankName, edtBSB, edtAccountNumber, edtStreetName, edtSuburb, edtState, edtPostCode, edtPhone, edtEmail;
    private ButtonCustom btnNext;
    private Bundle bundle = new Bundle();
    private ImageView imgEdit;
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
        imgEdit = (ImageView) findViewById(R.id.img_edit);
        imgEdit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        bundle = getArguments();
        personalInfomationResponse = (PersonalInfomationResponse) bundle.getSerializable(Constants.PERSONNAL_INFO_EXTRA);

        LogUtils.d(TAG, "initData , personalInfomationResponse : " + personalInfomationResponse.toString());
        updatePersonalInfo();
    }

    @Override
    protected void resumeData() {

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

    private void donext() {

        if (edtBankName.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtBankName, Gravity.BOTTOM, getString(R.string.valid_app_bank_name),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtBSB.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtBSB, Gravity.BOTTOM, getString(R.string.valid_app_bsb),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtAccountNumber.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtAccountNumber, Gravity.TOP, getString(R.string.valid_app_account_number),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtStreetName.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtStreetName, Gravity.TOP, getString(R.string.valid_app_street_name),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtSuburb.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtSuburb, Gravity.TOP, getString(R.string.valid_app_suburb),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtState.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtState, Gravity.TOP, getString(R.string.valid_app_state),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtPostCode.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtPostCode, Gravity.TOP, getString(R.string.valid_app_post_code),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtPhone.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtPhone, Gravity.TOP, getString(R.string.valid_app_phone),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtEmail.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtEmail, Gravity.TOP, getString(R.string.valid_app_email),
                    ContextCompat.getColor(getContext(), R.color.red));
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

        LogUtils.d(TAG, "donext jsonRequest : " + jsonRequest.toString());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().updatePersonalInfo(UserManager.getUserToken(), bundle.getInt(Constants.PARAMETER_APP_ID), body).enqueue(new Callback<PersonalInfomationResponse>() {
            @Override
            public void onResponse(Call<PersonalInfomationResponse> call, Response<PersonalInfomationResponse> response) {
                LogUtils.d(TAG, "donext code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "donext body : " + response.body().toString());
//                    bundle.putSerializable(Constants.PERSONNAL_INFO_EXTRA, response.body());
//                    openFragment(R.id.layout_container, ReviewPersonalInfomationB.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "donext error : " + error.message());
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
                LogUtils.e(TAG, "donext onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        donext();
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
            case R.id.img_edit:
                doEdit();
                break;

            case R.id.btn_next:
                donext();
                break;
        }
    }
}
