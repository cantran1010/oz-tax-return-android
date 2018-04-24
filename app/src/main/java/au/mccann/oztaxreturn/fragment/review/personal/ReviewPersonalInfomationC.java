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
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.PersonalInfomationResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TooltipUtils;
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
    private Bundle bundle;
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

        bundle = getArguments();
        personalInfomationResponse = (PersonalInfomationResponse) bundle.getSerializable(Constants.PERSONNAL_INFO_EXTRA);

        if (personalInfomationResponse.getStudentLoan() > 0) {
            layoutRemain.setVisibility(View.VISIBLE);
            edtLoan.setText(personalInfomationResponse.getStudentLoan() + "");
        } else {
            layoutRemain.setVisibility(View.GONE);
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

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

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
        ApiClient.getApiService().updatePersonalInfo(UserManager.getUserToken(), bundle.getInt(Constants.PARAMETER_APP_ID), body).enqueue(new Callback<PersonalInfomationResponse>() {
            @Override
            public void onResponse(Call<PersonalInfomationResponse> call, Response<PersonalInfomationResponse> response) {
                LogUtils.d(TAG, "doNextC code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doNextC body : " + response.body().toString());
//                    bundle.putSerializable(Constants.PERSONNAL_INFO_EXTRA, response.body());
//                    openFragment(R.id.layout_container, ReviewPersonalInfomationB.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_next:
                doNextC();
                break;

            case R.id.img_edit:
                doEdit();
                break;


        }
    }
}
