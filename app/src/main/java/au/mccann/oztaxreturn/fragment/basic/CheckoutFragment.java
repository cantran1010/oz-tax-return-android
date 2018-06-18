package au.mccann.oztaxreturn.fragment.basic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.SplashActivity;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.dialog.AlertDialogOkNonTouch;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.fragment.HomeFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.FeeResponse;
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

/**
 * Created by LongBui on 5/2/18.
 */

public class CheckoutFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = CheckoutFragment.class.getSimpleName();
    private FeeResponse feeResponse;
    protected static final String PUBLISH_KEY = "pk_test_G2whU73GFsa4omTXcT54gQoY";
    private EdittextCustom edtName, edtNumber, edtMonth, edtYear, edtCvm;

    @Override
    protected int getLayout() {
        return R.layout.checkout_fragment;
    }

    @Override
    protected void initView() {
        ButtonCustom btnCheckout = (ButtonCustom) findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(this);

        edtName = (EdittextCustom) findViewById(R.id.edt_name);
        edtNumber = (EdittextCustom) findViewById(R.id.edt_number);
        edtMonth = (EdittextCustom) findViewById(R.id.edt_month);
        edtYear = (EdittextCustom) findViewById(R.id.edt_year);
        edtCvm = (EdittextCustom) findViewById(R.id.edt_cvm);

    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.first_checkout_title));
        appBarVisibility(true, true, 2);
        getBaseProgress(getApplicationResponse());

        feeResponse = (FeeResponse) getArguments().getSerializable(Constants.PARAMETER_FEE_EXTRA);
        LogUtils.d(TAG, "feeResponse : " + feeResponse.toString());
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    private void doNext() {

        if (edtName.getText().toString().trim().isEmpty()) {
            Utils.showToolTip(getActivity(), edtName, getString(R.string.vali_all_empty));
            return;
        }

        if (edtNumber.getText().toString().trim().isEmpty()) {
            Utils.showToolTip(getActivity(), edtNumber, getString(R.string.vali_all_empty));
            return;
        }

        if (edtMonth.getText().toString().trim().isEmpty()) {
            Utils.showToolTip(getActivity(), edtMonth, getString(R.string.vali_all_empty));
            return;
        }

        if (edtYear.getText().toString().trim().isEmpty()) {
            Utils.showToolTip(getActivity(), edtYear, getString(R.string.vali_all_empty));
            return;
        }

        if (edtCvm.getText().toString().trim().isEmpty()) {
            Utils.showToolTip(getActivity(), edtCvm, getString(R.string.vali_all_empty));
            return;
        }

        ProgressDialogUtils.showProgressDialog(getActivity());
//        final Card cardToSave = new Card("4242424242424242", 12, 2019, "369");
        final Card cardToSave = new Card(edtNumber.getText().toString(), Integer.valueOf(edtMonth.getText().toString()), Integer.valueOf(edtYear.getText().toString()), edtCvm.getText().toString(),
                edtName.getText().toString(), null, null, null, null, null, null, null);
        final Stripe stripe = new Stripe(getActivity(), PUBLISH_KEY);
        stripe.createToken(
                cardToSave,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        // Send token to your server
                        // Token is created using Checkout or Elements!
                        // Get the payment token ID submitted by the form:

                        Log.d(TAG, "doNext token : " + token.toString());
                        doCheckout(token.getId());
                    }

                    public void onError(Exception error) {
                        Log.e(TAG, "doNext error to string : " + error.toString());
                        Log.e(TAG, "doNext errot msg : " + error.getLocalizedMessage());
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.getLocalizedMessage(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                        ProgressDialogUtils.dismissProgressDialog();
                    }

                }
        );
    }

    private void doCheckout(final String token) {
        ProgressDialogUtils.showProgressDialog(getActivity());
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("stripeToken", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "doCheckout jsonRequest : " + jsonRequest.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().checkout(UserManager.getUserToken(), getApplicationResponse().getId(), body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "doCheckout code : " + response.code());

                if (response.code() == Constants.HTTP_CODE_NO_CONTENT) {
//                    Utils.showLongToast(getActivity(), getString(R.string.checkout_success), false, false);

                    DialogUtils.showOkDialogNonTouch(getActivity(), getString(R.string.app_name_old), getString(R.string.checkout_success), getString(R.string.ok), new AlertDialogOkNonTouch.AlertDialogListener() {
                        @Override
                        public void onSubmit() {
                            openFragment(R.id.layout_container, HomeFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                        }
                    });

                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "doCheckout error : " + error.status());
                    LogUtils.d(TAG, "doCheckout error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.PARAMETER_FEE_EXTRA, feeResponse);
                                openFragment(R.id.layout_container, CheckoutFragment.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LogUtils.e(TAG, "doCheckout onFailure : " + t.getMessage());
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doCheckout(token);
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

            case R.id.btn_checkout:
                doNext();
                break;

        }
    }
}
