package au.mccann.oztaxreturn.fragment.review.personal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.PersonalInfomationResponse;
import au.mccann.oztaxreturn.utils.DateTimeUtils;
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
 * Created by LongBui on 4/23/18.
 */

public class ReviewPersonalInfomationA extends BaseFragment implements View.OnClickListener {

    private static final String TAG = ReviewPersonalInfomationA.class.getSimpleName();
    private EdittextCustom edtTitle, edtFirstName, edtMidName, edtLastName, edtBirthDay;
    private Spinner spGender;
    private RadioButtonCustom rbYes, rbNo;
    private Calendar calendar = GregorianCalendar.getInstance();
    private List<String> genders = new ArrayList<>();
    private Bundle bundle = new Bundle();
    private ImageView imgEdit;

    @Override
    protected int getLayout() {
        return R.layout.review_personal_infomation_a_fragment;
    }

    @Override
    protected void initView() {
        edtTitle = (EdittextCustom) findViewById(R.id.edt_title);
        edtFirstName = (EdittextCustom) findViewById(R.id.edt_first_name);
        edtMidName = (EdittextCustom) findViewById(R.id.edt_middle_name);
        edtLastName = (EdittextCustom) findViewById(R.id.edt_last_name);
        edtBirthDay = (EdittextCustom) findViewById(R.id.tv_birthday);
        rbYes = (RadioButtonCustom) findViewById(R.id.rb_yes);
        rbNo = (RadioButtonCustom) findViewById(R.id.rb_no);
        edtBirthDay.setOnClickListener(this);
        imgEdit = (ImageView) findViewById(R.id.img_edit);
        imgEdit.setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.infomation_a));
        appBarVisibility(true, true);

        bundle = getArguments();
        getReviewInformation();
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    private void openDatePicker() {
        @SuppressWarnings("deprecation") DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        if (view.isShown()) {
                            calendar.set(year, monthOfYear, dayOfMonth);
                            edtBirthDay.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    private void getReviewInformation() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        ApiClient.getApiService().getReviewPersonalInfo(UserManager.getUserToken(), bundle.getInt(Constants.PARAMETER_APP_ID)).enqueue(new Callback<PersonalInfomationResponse>() {
            @Override
            public void onResponse(Call<PersonalInfomationResponse> call, Response<PersonalInfomationResponse> response) {
                LogUtils.d(TAG, "getReviewInformation code : " + response.code());

                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getReviewInformation body : " + response.body().toString());
                    try {
                        calendar = DateTimeUtils.toCalendarBirthday(response.body().getBirthday());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    updatePersonalInfomation(response.body());
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "getReviewInformation error : " + error.message());
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
                LogUtils.e(TAG, "getReviewInformation onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getReviewInformation();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private void donext() {

        if (edtTitle.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtTitle, Gravity.TOP, getString(R.string.valid_title),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }

        if (edtFirstName.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtFirstName, Gravity.TOP, getString(R.string.valid_first_name),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }

        if (edtMidName.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtMidName, Gravity.TOP, getString(R.string.valid_mid_name),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }

        if (edtLastName.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtLastName, Gravity.TOP, getString(R.string.valid_last_name),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }

        if (edtBirthDay.getText().toString().trim().isEmpty()) {
            TooltipUtils.showToolTipView(getContext(), edtBirthDay, Gravity.TOP, getString(R.string.valid_birth_day),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }

        ProgressDialogUtils.showProgressDialog(getActivity());
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("title", edtTitle.getText().toString().trim());
            jsonRequest.put("first_name", edtFirstName.getText().toString().trim());
            jsonRequest.put("middle_name", edtMidName.getText().toString().trim());
            jsonRequest.put("last_name", edtLastName.getText().toString().trim());
            jsonRequest.put("birthday", DateTimeUtils.fromCalendarToBirthday(calendar));
            jsonRequest.put("local", rbYes.isChecked());

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
                    bundle.putSerializable(Constants.PERSONNAL_INFO_EXTRA, response.body());
                    openFragment(R.id.layout_container, ReviewPersonalInfomationB.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
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

    private void doEdit() {
        edtTitle.setEnabled(true);
        edtFirstName.setEnabled(true);
        edtMidName.setEnabled(true);
        edtLastName.setEnabled(true);
        edtBirthDay.setEnabled(true);
        rbYes.setEnabled(true);
        rbNo.setEnabled(true);
    }

    private void updatePersonalInfomation(PersonalInfomationResponse personalInfomationResponse) {
        edtTitle.setText(personalInfomationResponse.getTitle());
        edtFirstName.setText(personalInfomationResponse.getFirstName());
        edtMidName.setText(personalInfomationResponse.getMiddleName());
        edtLastName.setText(personalInfomationResponse.getLastName());
        edtBirthDay.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));

        if (personalInfomationResponse.isLocal()) {
            rbYes.setChecked(true);
            rbNo.setChecked(false);
        } else {
            rbYes.setChecked(false);
            rbNo.setChecked(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_birthday:
                openDatePicker();
                break;
            case R.id.btn_next:
                donext();
                break;
            case R.id.img_edit:
                doEdit();
                break;
        }
    }

}
