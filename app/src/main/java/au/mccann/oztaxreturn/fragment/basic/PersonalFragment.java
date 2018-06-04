package au.mccann.oztaxreturn.fragment.basic;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import au.mccann.oztaxreturn.R;
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
import au.mccann.oztaxreturn.utils.DateTimeUtils;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.RadioButtonCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static au.mccann.oztaxreturn.utils.DateTimeUtils.getDateBirthDayFromIso;
import static au.mccann.oztaxreturn.utils.Utils.showToolTip;


/**
 * Created by CanTran on 4/19/18.
 */
public class PersonalFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = PersonalFragment.class.getSimpleName();
    private EdittextCustom edtFirstName, edtMidName, edtLastName, edtBirthDay;
    private Spinner spGender,spTitle;
    private RadioButtonCustom rbYes, rbNo;
    private Calendar calendar = GregorianCalendar.getInstance();
    private List<String> genders = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.fragment_personal_information;
    }

    @Override
    protected void initView() {
        edtFirstName = (EdittextCustom) findViewById(R.id.edt_first_name);
        edtMidName = (EdittextCustom) findViewById(R.id.edt_middle_name);
        edtLastName = (EdittextCustom) findViewById(R.id.edt_last_name);
        edtBirthDay = (EdittextCustom) findViewById(R.id.tv_birthday);
        spGender = (Spinner) findViewById(R.id.sp_gender);
        spTitle = (Spinner) findViewById(R.id.sp_title);
        rbYes = (RadioButtonCustom) findViewById(R.id.rb_yes);
        rbNo = (RadioButtonCustom) findViewById(R.id.rb_no);
        edtBirthDay.setOnClickListener(this);


    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.personal_information_title));
        appBarVisibility(true, true, 2);
        getBaseProgress(getApplicationResponse());
        genders = Arrays.asList(getResources().getStringArray(R.array.string_array_gender));
        titles = Arrays.asList(getResources().getStringArray(R.array.string_array_title));
        OzSpinnerAdapter dataNameAdapter = new OzSpinnerAdapter(getContext(), genders, 0);
        OzSpinnerAdapter dataTitleAdapter = new OzSpinnerAdapter(getContext(), titles, 0);
        spTitle.setAdapter(dataTitleAdapter);
        spGender.setAdapter(dataNameAdapter);
        findViewById(R.id.btn_next).setOnClickListener(this);
        getBasicInformation();
    }

    private void updateUI(PersonalInformation pf) {
        for (int i = 0; i < titles.size(); i++) {
            if (pf.getTitle().equalsIgnoreCase(titles.get(i))) {
                spTitle.setSelection(i);
                break;
            }
        }
        edtFirstName.setText(pf.getFirstName());
        edtMidName.setText(pf.getMiddleName());
        edtLastName.setText(pf.getLastName());
        if (pf.getBirthday() != null) edtBirthDay.setText(getDateBirthDayFromIso(pf.getBirthday()));
        for (int i = 0; i < genders.size(); i++) {
            if (pf.getGender().equalsIgnoreCase(genders.get(i))) {
                spGender.setSelection(i);
                break;
            } else
                spGender.setSelection(2);
        }
        if (pf.isLocal())
            rbYes.setChecked(true);
        else
            rbNo.setChecked(true);
    }

    private void getBasicInformation() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        LogUtils.d(TAG, "getBasicInformation code : " + getApplicationResponse().getId());
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
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_TITLE, spTitle.getSelectedItem().toString());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_FIRST_NAME, edtFirstName.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_MIDDLE_NAME, edtMidName.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_LAST_NAME, edtLastName.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_BIRTHDAY, DateTimeUtils.fromCalendarToBirthday(calendar));
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_GENDER, spGender.getSelectedItem().toString().toLowerCase());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_LOCAL, rbYes.isChecked());
            jsonRequest.put(Constants.PARAMETER_BASIC_INFO, salaryJson);
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
                    openFragment(R.id.layout_container, SubmitFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
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

    private void openDatePicker() {
//        LogUtils.d(TAG, "openDatePicker" + DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));
        @SuppressWarnings("deprecation") DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        if (view.isShown()) {
                            calendar.set(year, monthOfYear, dayOfMonth);
//                            edtBirthDay.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));
                            edtBirthDay.setText(DateTimeUtils.fromCalendarToView(calendar));
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    private void donext() {
        if (edtFirstName.getText().toString().trim().isEmpty()) {
            edtFirstName.requestFocus();
            showToolTip(getContext(), edtFirstName, getString(R.string.vali_all_empty));
            return;
        }
        if (edtMidName.getText().toString().trim().isEmpty()) {
            edtMidName.requestFocus();
            showToolTip(getContext(), edtMidName, getString(R.string.vali_all_empty));
            return;
        }
        if (edtLastName.getText().toString().trim().isEmpty()) {
            edtLastName.requestFocus();
            showToolTip(getContext(), edtLastName, getString(R.string.vali_all_empty));
            return;
        }
        if (edtBirthDay.getText().toString().trim().isEmpty()) {
            showToolTip(getContext(), edtBirthDay, getString(R.string.vali_all_empty));
            return;
        }
        doSaveBasic();
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

        }

    }
}
