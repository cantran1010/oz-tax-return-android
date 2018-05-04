package au.mccann.oztaxreturn.fragment.review.family;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;

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
import au.mccann.oztaxreturn.rest.response.DependantsResponse;
import au.mccann.oztaxreturn.rest.response.ReviewFamilyHealthResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TooltipUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.CheckBoxCustom;
import au.mccann.oztaxreturn.view.EdittextCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 5/3/18.
 */

public class ReviewFamilyHealthDependantsFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = ReviewFamilyHealthDependantsFragment.class.getSimpleName();
    private ImageView imgEdit;
    private CheckBoxCustom cbYes, cbNo;
    private LinearLayout layoutYes;
    private EdittextCustom edtNumber;
    private ReviewFamilyHealthResponse reviewFamilyHealthResponse;

    @Override
    protected int getLayout() {
        return R.layout.review_family_health_dependants_fragment;
    }

    @Override
    protected void initView() {
        imgEdit = (ImageView) findViewById(R.id.img_edit);
        imgEdit.setOnClickListener(this);

        cbYes = (CheckBoxCustom) findViewById(R.id.cb_yes);
        cbNo = (CheckBoxCustom) findViewById(R.id.cb_no);

        layoutYes = (LinearLayout) findViewById(R.id.layout_yes);
        edtNumber = (EdittextCustom) findViewById(R.id.edt_number_dependants);

        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.review_fhd_title));
        appBarVisibility(true, true,1);

        cbYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cbNo.setChecked(false);
                    layoutYes.setVisibility(View.VISIBLE);
                }
            }
        });

        cbNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cbYes.setChecked(false);
                    layoutYes.setVisibility(View.GONE);
                }
            }
        });

        getReviewFamilyAndHealth();
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    private void updateUI() {
        if (reviewFamilyHealthResponse.getDependantsResponse().isHad()) {
            layoutYes.setVisibility(View.VISIBLE);
            edtNumber.setText(reviewFamilyHealthResponse.getDependantsResponse().getNumber() + "");

            cbYes.setChecked(true);
            cbNo.setChecked(false);
        } else {
            layoutYes.setVisibility(View.GONE);

            cbYes.setChecked(false);
            cbNo.setChecked(true);
        }
    }

    private void getReviewFamilyAndHealth() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        LogUtils.d(TAG, "getReviewFamilyAndHealth getApplicationResponse().getId() : " + getApplicationResponse().getId());

        ApiClient.getApiService().getReviewFamilyHealth(UserManager.getUserToken(), getApplicationResponse().getId()).enqueue(new Callback<ReviewFamilyHealthResponse>() {
            @Override
            public void onResponse(Call<ReviewFamilyHealthResponse> call, Response<ReviewFamilyHealthResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getReviewFamilyAndHealth code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getReviewFamilyAndHealth body : " + response.body().toString());
                    reviewFamilyHealthResponse = response.body();
                    updateUI();
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getReviewFamilyAndHealth error : " + error.message());
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<ReviewFamilyHealthResponse> call, Throwable t) {
                LogUtils.e(TAG, "getReviewFamilyAndHealth onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getReviewFamilyAndHealth();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private void doNext() {

        if (cbYes.isChecked()) {
            if (edtNumber.getText().toString().trim().isEmpty()) {
                TooltipUtils.showToolTipView(getContext(), edtNumber, Gravity.TOP, getString(R.string.valid_number),
                        ContextCompat.getColor(getContext(), R.color.red));
                return;
            }
        }

        DependantsResponse dependantsResponse = new DependantsResponse();
        dependantsResponse.setHad(cbYes.isChecked());

        if (cbYes.isChecked())
            dependantsResponse.setNumber(Integer.valueOf(edtNumber.getText().toString()));

        Gson gson = new Gson();
        String jsonInString = gson.toJson(dependantsResponse);

        ProgressDialogUtils.showProgressDialog(getActivity());
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("dependants", new JSONObject(jsonInString));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtils.d(TAG, "doNext jsonRequest : " + jsonRequest.toString());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().updateReviewFamilyHealth(UserManager.getUserToken(), getApplicationResponse().getId(), body).enqueue(new Callback<ReviewFamilyHealthResponse>() {
            @Override
            public void onResponse(Call<ReviewFamilyHealthResponse> call, Response<ReviewFamilyHealthResponse> response) {
                LogUtils.d(TAG, "doNext code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doNext body : " + response.body().toString());
                    openFragment(R.id.layout_container, ReviewFamilyHealthMedicareFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "doNext error : " + error.message());
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
            public void onFailure(Call<ReviewFamilyHealthResponse> call, Throwable t) {
                LogUtils.e(TAG, "doNext onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doNext();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private void doEdit() {
        cbYes.setEnabled(true);
        cbNo.setEnabled(true);
        edtNumber.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_next:
                doNext();
                break;

            case R.id.img_edit:
                doEdit();
                break;

        }
    }

}
