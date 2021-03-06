package au.mccann.oztaxreturn.fragment.basic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.HomeActivity;
import au.mccann.oztaxreturn.activity.SplashActivity;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.dialog.AlertDialogOkNonTouch;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.FeeResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.TextViewCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 4/19/18.
 */

public class FirstCheckoutFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = FirstCheckoutFragment.class.getSimpleName();
    private TextViewCustom tvServiceFee, tvTotalFee;
    private EdittextCustom edtPromotionCode;
    private FeeResponse feeResponse;
    private ImageView imgChecked;


    @Override
    protected int getLayout() {
        return R.layout.first_checkout_fragment;
    }

    @Override
    protected void initView() {
        tvServiceFee = (TextViewCustom) findViewById(R.id.tv_service_fee);
        tvTotalFee = (TextViewCustom) findViewById(R.id.tv_total_fee);
        edtPromotionCode = (EdittextCustom) findViewById(R.id.edt_promotion_code);
        imgChecked = (ImageView) findViewById(R.id.img_checked);

        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        ((HomeActivity) getActivity()).setIndex(31);
        setTitle(getString(R.string.first_checkout_title));
        appBarVisibility(true, true, 2);
        getBaseProgress(getApplicationResponse());
        getPromotionFee();
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    private void doNext() {
        checkPromotionCode();
    }

    private void checkPromotionCode() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("code", edtPromotionCode.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtils.d(TAG, "checkPromotionCode jsonRequest : " + jsonRequest.toString());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().checkPromotionCode(UserManager.getUserToken(), getApplicationResponse().getId(), body).enqueue(new Callback<FeeResponse>() {
            @Override
            public void onResponse(Call<FeeResponse> call, Response<FeeResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "checkPromotionCode code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "checkPromotionCode body : " + response.body().toString());
                    feeResponse = response.body();
                    tvServiceFee.setText(String.valueOf(feeResponse.getAmount()));
                    tvTotalFee.setText(String.valueOf(feeResponse.getAmountAfter()));
                    if (feeResponse.getPromotionCode().isEmpty())
                        imgChecked.setVisibility(View.GONE);
                    else if (feeResponse.isPromotionValid()) imgChecked.setVisibility(View.VISIBLE);
                    if (feeResponse.isPromotionValid() && feeResponse.getPromotionCode().isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.PARAMETER_FEE_EXTRA, feeResponse);
                        openFragment(R.id.layout_container, CheckoutFragment.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
                    } else
                        DialogUtils.showOkDialogNonTouch(getActivity(), getString(R.string.app_name_old), getString(R.string.promotion_ok), getString(R.string.ok), new AlertDialogOkNonTouch.AlertDialogListener() {
                            @Override
                            public void onSubmit() {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.PARAMETER_FEE_EXTRA, feeResponse);
                                openFragment(R.id.layout_container, CheckoutFragment.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
                            }
                        });

                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "checkPromotionCode error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkAndCancelDialog(getActivity(), getString(R.string.app_name_old), getString(R.string.promotion_error), getString(R.string.Yes), getString(R.string.No), new AlertDialogOkAndCancel.AlertDialogListener() {
                            @Override
                            public void onSubmit() {
                                edtPromotionCode.setText("");
                                checkPromotionCode();
                            }

                            @Override
                            public void onCancel() {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.PARAMETER_FEE_EXTRA, feeResponse);
                                openFragment(R.id.layout_container, CheckoutFragment.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<FeeResponse> call, Throwable t) {
                LogUtils.e(TAG, "checkPromotionCode onFailure : " + t.getMessage());
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        checkPromotionCode();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                ProgressDialogUtils.dismissProgressDialog();
            }
        });
    }

    private void getPromotionFee() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        ApiClient.getApiService().getPromotionFee(UserManager.getUserToken(), getApplicationResponse().getId()).enqueue(new Callback<FeeResponse>() {
            @Override
            public void onResponse(Call<FeeResponse> call, Response<FeeResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getPromotionFee code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "checkPromotionCode body : " + response.body().toString());
                    feeResponse = response.body();
                    tvServiceFee.setText(Utils.displayCurrency(String.valueOf(feeResponse.getAmount())));
                    tvTotalFee.setText(Utils.displayCurrency(String.valueOf(feeResponse.getAmountAfter())));
                    edtPromotionCode.setText(feeResponse.getPromotionCode());
                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "getPromotionFee error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<FeeResponse> call, Throwable t) {
                LogUtils.e(TAG, "getPromotionFee onFailure : " + t.getMessage());
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getPromotionFee();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                ProgressDialogUtils.dismissProgressDialog();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                doNext();
                break;

        }
    }
}
