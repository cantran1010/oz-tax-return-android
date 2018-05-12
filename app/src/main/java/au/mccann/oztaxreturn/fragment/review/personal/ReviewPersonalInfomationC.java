package au.mccann.oztaxreturn.fragment.review.personal;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.fragment.review.income.ReviewIncomeWS;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.PersonalInfomationResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TooltipUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.RadioButtonCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 4/24/18.
 */

public class ReviewPersonalInfomationC extends BaseFragment implements View.OnClickListener {

    private static final String TAG = ReviewPersonalInfomationC.class.getSimpleName();
    private RadioButtonCustom rbYes, rbNo;
    private LinearLayout layoutRemain;
    private PersonalInfomationResponse personalInfomationResponse;
    private EdittextCustom edtLoan;
    private ImageView imgEdit;

    @Override
    protected int getLayout() {
        return R.layout.review_begin_c_fragment;
    }

    @Override
    protected void initView() {
        rbYes = (RadioButtonCustom) findViewById(R.id.rb_yes);
        rbNo = (RadioButtonCustom) findViewById(R.id.rb_no);
        findViewById(R.id.btn_next).setOnClickListener(this);
        layoutRemain = (LinearLayout) findViewById(R.id.remain_layout);
        edtLoan = (EdittextCustom) findViewById(R.id.edt_loan);
        imgEdit = (ImageView) findViewById(R.id.img_edit);
        imgEdit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        imgEdit.setEnabled(isEditApp());
        getReviewInformationC();
        getReviewProgress(getApplicationResponse());
    }

    @Override
    protected void resumeData() {
        appBarVisibility(true, true, 1);
    }

    @Override
    public void onRefresh() {

    }

    private void updatePersonalInfo() {
        if (personalInfomationResponse.getStudentLoan() > 0) {
            layoutRemain.setVisibility(View.VISIBLE);
            edtLoan.setText(personalInfomationResponse.getStudentLoan() + "");
            rbYes.setChecked(true);
            rbNo.setChecked(false);
        } else {
            layoutRemain.setVisibility(View.GONE);
            rbYes.setChecked(false);
            rbNo.setChecked(true);
        }

        rbYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) layoutRemain.setVisibility(View.VISIBLE);
                else
                    layoutRemain.setVisibility(View.GONE);
            }
        });

        rbNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) layoutRemain.setVisibility(View.GONE);
                else
                    layoutRemain.setVisibility(View.VISIBLE);
            }
        });
    }

    private void doNextC() {

        if (rbYes.isChecked() && edtLoan.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtLoan, Gravity.BOTTOM, getString(R.string.valid_app_bank_name),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }

        ProgressDialogUtils.showProgressDialog(getActivity());


        JSONObject jsonRequest = new JSONObject();
        try {
            if (rbYes.isChecked())
                jsonRequest.put("student_loan", edtLoan.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtils.d(TAG, "doNextC jsonRequest : " + jsonRequest.toString());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().updatePersonalInfo(UserManager.getUserToken(), getApplicationResponse().getId(), body).enqueue(new Callback<PersonalInfomationResponse>() {
            @Override
            public void onResponse(Call<PersonalInfomationResponse> call, Response<PersonalInfomationResponse> response) {
                LogUtils.d(TAG, "doNextC code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doNextC body : " + response.body().toString());
//                    bundle.putSerializable(Constants.PERSONNAL_INFO_EXTRA, response.body());
                    openFragment(R.id.layout_container, ReviewIncomeWS.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "doNextC error : " + error.message());
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
                LogUtils.e(TAG, "doNextC onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doNextC();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private void doEdit() {
        rbYes.setEnabled(true);
        rbNo.setEnabled(true);
        edtLoan.setEnabled(true);
    }

    private void getReviewInformationC() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        ApiClient.getApiService().getReviewPersonalInfo(UserManager.getUserToken(), getApplicationResponse().getId()).enqueue(new Callback<PersonalInfomationResponse>() {
            @Override
            public void onResponse(Call<PersonalInfomationResponse> call, Response<PersonalInfomationResponse> response) {
                LogUtils.d(TAG, "getReviewInformationC code : " + response.code());

                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getReviewInformationC body : " + response.body().toString());
                    try {
                        personalInfomationResponse = response.body();
                        updatePersonalInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "getReviewInformationC error : " + error.message());
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
                LogUtils.e(TAG, "getReviewInformationC onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getReviewInformationC();
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

            case R.id.btn_next:
                if (isEditApp()) doNextC();
                else
                    openFragment(R.id.layout_container, ReviewIncomeWS.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;

            case R.id.img_edit:
                doEdit();
                break;


        }
    }
}
