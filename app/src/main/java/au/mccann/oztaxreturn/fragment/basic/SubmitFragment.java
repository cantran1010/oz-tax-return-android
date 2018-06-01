package au.mccann.oztaxreturn.fragment.basic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Spinner;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.GeneralInfoActivity;
import au.mccann.oztaxreturn.activity.SplashActivity;
import au.mccann.oztaxreturn.adapter.OzSpinnerAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.PersonalInformation;
import au.mccann.oztaxreturn.model.ResponseBasicInformation;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.CountryCodeResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.ButtonCustom;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.TextViewCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static au.mccann.oztaxreturn.utils.Utils.showToolTip;


/**
 * Created by CanTran on 4/19/18.
 */
public class SubmitFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SubmitFragment.class.getSimpleName();
    private EdittextCustom edtBankName, edtBSB, edtAccountNumber, edtStreetName, edtSuburb, edtPostCode, edtPhone, edtEmail;
    private TextViewCustom tvNote;
    private Spinner spCountryCode, spState;
    private ArrayList<CountryCodeResponse> countryCodeResponses;
    private List<String> states = new ArrayList<>();


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
        spState = (Spinner) findViewById(R.id.sp_state);
        edtPostCode = (EdittextCustom) findViewById(R.id.edt_post_code);
        edtPhone = (EdittextCustom) findViewById(R.id.edt_phone);
        edtEmail = (EdittextCustom) findViewById(R.id.edt_email);
        ButtonCustom btnSubmit = (ButtonCustom) findViewById(R.id.btn_submit);
        tvNote = (TextViewCustom) findViewById(R.id.tv_note);
        btnSubmit.setOnClickListener(this);
        spCountryCode = (Spinner) findViewById(R.id.sp_country_code);
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.personal_information_title));
        appBarVisibility(true, true, 2);
        states = Arrays.asList(getResources().getStringArray(R.array.string_array_states));
        OzSpinnerAdapter dataNameAdapter = new OzSpinnerAdapter(getActivity(), states, 0);
        spState.setAdapter(dataNameAdapter);
        getCountryCode();

    }

    private void getCountryCode() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        ApiClient.getApiService().getCountryCodeResponse(UserManager.getUserToken()).enqueue(new Callback<List<CountryCodeResponse>>() {
            @Override
            public void onResponse(Call<List<CountryCodeResponse>> call, Response<List<CountryCodeResponse>> response) {
                LogUtils.d(TAG, "getCountryCode code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getCountryCode body : " + response.body().toString());
                    countryCodeResponses = (ArrayList<CountryCodeResponse>) response.body();
                    ArrayList<String> listCode = new ArrayList<>();
                    for (CountryCodeResponse countryCodeResponse : countryCodeResponses) {
                        listCode.add(countryCodeResponse.getDialCode());
                    }
                    OzSpinnerAdapter dataNameAdapter = new OzSpinnerAdapter(getContext(), listCode, 0);
                    spCountryCode.setAdapter(dataNameAdapter);
                    getBasicInformation();
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getCountryCode error : " + error.message());
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.notification_title), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CountryCodeResponse>> call, Throwable t) {

            }
        });
    }

    private void updateUI(PersonalInformation pf) {
        edtBankName.setText(pf.getBankAccountName());
        edtAccountNumber.setText(pf.getBankAccountNumber());
        edtBSB.setText(pf.getBankAccountBsb());
        edtStreetName.setText(pf.getStreet());
        edtSuburb.setText(pf.getSuburb());
//        edtPhone.setText(pf.getPhone());
        edtEmail.setText(pf.getEmail());
        edtPostCode.setText(pf.getPostcode());
        for (int i = 0; i < states.size(); i++) {
            if (pf.getState().equalsIgnoreCase(states.get(i))) {
                spState.setSelection(i);
                break;
            }
        }
        setUnderLinePolicy(tvNote);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(pf.getPhone(), null);

            int code = phoneNumber.getCountryCode();
            LogUtils.d(TAG, "updateUI code : " + code);

            for (int i = 0; i < countryCodeResponses.size(); i++) {
                if (countryCodeResponses.get(i).getDialCode().equals(("+") + code)) {
                    spCountryCode.setSelection(i);
                    break;
                }
            }
            edtPhone.setText(pf.getPhone().substring(1 + String.valueOf(code).length()));

        } catch (NumberParseException e) {
            LogUtils.e(TAG, "updateUI was thrown: " + e.toString());
        }
    }

    private void getBasicInformation() {
        ApiClient.getApiService().getBasicInformation(UserManager.getUserToken(), getApplicationResponse().getId()).enqueue(new Callback<ResponseBasicInformation>() {
            @Override
            public void onResponse(Call<ResponseBasicInformation> call, Response<ResponseBasicInformation> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getBasicInformation code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getBasicInformation body : " + response.body().toString());
                    if (response.body().getPersonalInformation() != null)
                        updateUI(response.body().getPersonalInformation());
                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getBasicInformation error : " + error.message());
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.notification_title), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBasicInformation> call, Throwable t) {
                LogUtils.e(TAG, "getBasicInformation onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getBasicInformation();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
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
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_STATE, spState.getSelectedItem().toString());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_EMAIL, edtEmail.getText().toString().trim());
//            salaryJson.put(Constants.PARAMETER_BASIC_INFO_PHONE, formatPhoneNumber(edtPhone.getText().toString().trim()));
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_POST_CODE, edtPostCode.getText().toString().trim());
            jsonRequest.put(Constants.PARAMETER_BASIC_INFO, salaryJson);


            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber phone = phoneUtil.parse(edtPhone.getText().toString().trim(), countryCodeResponses.get(spCountryCode.getSelectedItemPosition()).getCode());
                String result = phoneUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.E164);
                salaryJson.put(Constants.PARAMETER_BASIC_INFO_PHONE, result);


                LogUtils.d(TAG, "doSaveBasic code : " + countryCodeResponses.get(spCountryCode.getSelectedItemPosition()).getCode());

            } catch (NumberParseException e) {
                LogUtils.e(TAG, "doSaveBasic ERROR : " + e.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "doSaveBasic jsonRequest : " + jsonRequest.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().saveBasicInformation(UserManager.getUserToken(), getApplicationResponse().getId(), body).enqueue(new Callback<ResponseBasicInformation>() {
            @Override
            public void onResponse(Call<ResponseBasicInformation> call, Response<ResponseBasicInformation> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "doSaveBasic code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    openFragment(R.id.layout_container, FirstCheckoutFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.e(TAG, "doSaveBasic error status: " + error.status());
                    LogUtils.e(TAG, "doSaveBasic error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(getContext(), getString(R.string.error), error.message().replace(".", " ").replace("_", " "), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
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
            edtBankName.requestFocus();
            showToolTip(getContext(), edtBankName, getString(R.string.vali_all_empty));
            return;
        }
        if (edtBSB.getText().toString().trim().isEmpty()) {
            edtBSB.requestFocus();
            showToolTip(getContext(), edtBSB, getString(R.string.vali_all_empty));
            return;
        }
        if (edtAccountNumber.getText().toString().trim().isEmpty()) {
            edtAccountNumber.requestFocus();
            showToolTip(getContext(), edtAccountNumber, getString(R.string.vali_all_empty));
            return;
        }
        if (edtStreetName.getText().toString().trim().isEmpty()) {
            edtStreetName.requestFocus();
            showToolTip(getContext(), edtStreetName, getString(R.string.vali_all_empty));
            return;
        }
        if (edtSuburb.getText().toString().trim().isEmpty()) {
            edtSuburb.requestFocus();
            showToolTip(getContext(), edtSuburb, getString(R.string.vali_all_empty));
            return;
        }
        if (edtPostCode.getText().toString().trim().isEmpty()) {
            edtPostCode.requestFocus();
            showToolTip(getContext(), edtPostCode, getString(R.string.vali_all_empty));
            return;
        }
        if (edtPhone.getText().toString().trim().isEmpty() && edtEmail.getText().toString().trim().isEmpty()) {
            edtPhone.requestFocus();
            showToolTip(getContext(), findViewById(R.id.btn_submit), getString(R.string.vali_phone_or_mail));
            return;
        }

        if (!edtEmail.getText().toString().trim().isEmpty() && !Utils.isValidEmail(edtEmail.getText().toString().trim())) {
            edtEmail.requestFocus();
            showToolTip(getContext(), edtEmail, getString(R.string.valid_app_email_2));
            return;
        }
        doSaveBasic();
    }


    private void setUnderLinePolicy(TextViewCustom textViewCustom) {
        String text = getString(R.string.privacy_policy);
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);
        ClickableSpan conditionClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                openGeneralInfoActivity(getString(R.string.app_term_conditions), "http://oztax.tonishdev.com/terms-and-conditions");
            }
        };
        ClickableSpan nadClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                openGeneralInfoActivity(getString(R.string.app_privacy_policy), "http://oztax.tonishdev.com/privacy-policy");
            }
        };
        ssBuilder.setSpan(
                new ForegroundColorSpan(Color.parseColor("#e55a1d")), // Span to add
                0, // Start of the span (inclusive)
                1, // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        ssBuilder.setSpan(
                new ForegroundColorSpan(Color.parseColor("#577bb5")), // Span to add
                text.indexOf(getString(R.string.app_privacy_policy)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.app_privacy_policy)) + String.valueOf(getString(R.string.app_privacy_policy)).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        ssBuilder.setSpan(
                nadClickableSpan,
                text.indexOf(getString(R.string.app_privacy_policy)),
                text.indexOf(getString(R.string.app_privacy_policy)) + String.valueOf(getString(R.string.app_privacy_policy)).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        ssBuilder.setSpan(
                new ForegroundColorSpan(Color.parseColor("#577bb5")), // Span to add
                text.indexOf(getString(R.string.app_term_conditions)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.app_term_conditions)) + String.valueOf(getString(R.string.app_term_conditions)).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        ssBuilder.setSpan(
                conditionClickableSpan, // Span to add
                text.indexOf(getString(R.string.app_term_conditions)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.app_term_conditions)) + String.valueOf(getString(R.string.app_term_conditions)).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        textViewCustom.setText(ssBuilder);
        textViewCustom.setMovementMethod(LinkMovementMethod.getInstance());
        textViewCustom.setHighlightColor(Color.TRANSPARENT);
    }

    private void openGeneralInfoActivity(String title, String url) {
        Intent intent = new Intent(getContext(), GeneralInfoActivity.class);
        intent.putExtra(Constants.URL_EXTRA, url);
        intent.putExtra(Constants.TITLE_INFO_EXTRA, title);
        startActivity(intent, TransitionScreen.RIGHT_TO_LEFT);
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
