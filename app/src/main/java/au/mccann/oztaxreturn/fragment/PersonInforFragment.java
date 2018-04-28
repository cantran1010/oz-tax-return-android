package au.mccann.oztaxreturn.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.adapter.OzSpinnerAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
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

import static au.mccann.oztaxreturn.utils.TooltipUtils.showToolTipView;

/**
 * Created by CanTran on 4/19/18.
 */
public class PersonInforFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = PersonInforFragment.class.getSimpleName();
    private EdittextCustom edtTitle, edtFirstName, edtMidName, edtLastName, edtBirthDay;
    private Spinner spGender;
    private RadioButtonCustom rbYes;
    private Calendar calendar = GregorianCalendar.getInstance();
    private List<String> genders = new ArrayList<>();
    private ResponseBasicInformation basic;
    private int appID;

    @Override
    protected int getLayout() {
        return R.layout.fragment_personal_information;
    }

    @Override
    protected void initView() {
        edtTitle = (EdittextCustom) findViewById(R.id.edt_title);
        edtFirstName = (EdittextCustom) findViewById(R.id.edt_first_name);
        edtMidName = (EdittextCustom) findViewById(R.id.edt_middle_name);
        edtLastName = (EdittextCustom) findViewById(R.id.edt_last_name);
        edtBirthDay = (EdittextCustom) findViewById(R.id.tv_birthday);
        spGender = (Spinner) findViewById(R.id.sp_gender);
        rbYes = (RadioButtonCustom) findViewById(R.id.rb_yes);
        edtBirthDay.setOnClickListener(this);


    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.personal_information_title));
        appBarVisibility(false, true,0);
        basic = (ResponseBasicInformation) getArguments().getSerializable(Constants.KEY_BASIC_INFORMATION);
        appID = basic.getAppId();
        LogUtils.d(TAG, "initData ResponseBasicInformation" + basic.toString());
        genders = Arrays.asList(getResources().getStringArray(R.array.string_array_gender));
        OzSpinnerAdapter dataNameAdapter = new OzSpinnerAdapter(getContext(), genders);
        spGender.setAdapter(dataNameAdapter);
        findViewById(R.id.btn_next).setOnClickListener(this);
        updateUI(basic);
    }

    private void updateUI(ResponseBasicInformation basic) {
        PersonalInformation pf = basic.getPersonalInformation();
        edtTitle.setText(pf.getTitle());
        edtFirstName.setText(pf.getFirstName());
        edtMidName.setText(pf.getMiddleName());
        edtLastName.setText(pf.getLastName());
        edtBirthDay.setText(pf.getBirthday());
        edtTitle.setText(pf.getTitle());
        for (int i = 0; i < genders.size(); i++) {
            if (pf.getGender().equalsIgnoreCase(genders.get(i))) spGender.setSelection(i);
            return;
        }
        rbYes.setChecked(pf.isLocal());
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
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_TITLE, edtTitle.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_FIRST_NAME, edtFirstName.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_MIDDLE_NAME, edtMidName.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_LAST_NAME, edtLastName.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_BIRTHDAY, edtBirthDay.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_GENDER, spGender.getSelectedItem().toString().toLowerCase());
            salaryJson.put(Constants.PARAMETER_BASIC_INFO_LOCAL, rbYes.isChecked());
            jsonRequest.put(Constants.PARAMETER_BASIC_INFO, salaryJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "doSaveBasic jsonRequest : " + jsonRequest.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().saveBasicInformation(UserManager.getUserToken(), appID, body).enqueue(new Callback<ResponseBasicInformation>() {
            @Override
            public void onResponse(Call<ResponseBasicInformation> call, Response<ResponseBasicInformation> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "doSaveBasic code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    basic = response.body();
                    basic.setAppId(appID);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.KEY_BASIC_INFORMATION, basic);
                    openFragment(R.id.layout_container, SubmitInformation.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.e(TAG, "doSaveBasic error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(getContext(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
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
        LogUtils.d(TAG, "openDatePicker" + DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));
        @SuppressWarnings("deprecation") DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        if (view.isShown()) {
                            calendar.set(year, monthOfYear, dayOfMonth);
                            edtBirthDay.setText(DateTimeUtils.fromCalendarToBirthday(calendar));
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    private void donext() {
        if (edtTitle.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtTitle, Gravity.BOTTOM, getString(R.string.valid_personal_title),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtFirstName.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtFirstName, Gravity.BOTTOM, getString(R.string.valid_first_name),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtMidName.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtMidName, Gravity.TOP, getString(R.string.valid_mid_name),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtLastName.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtLastName, Gravity.TOP, getString(R.string.valid_last_name),
                    ContextCompat.getColor(getContext(), R.color.red));
            return;
        }
        if (edtBirthDay.getText().toString().trim().isEmpty()) {
            showToolTipView(getContext(), edtBirthDay, Gravity.TOP, getString(R.string.valid_birth_day),
                    ContextCompat.getColor(getContext(), R.color.red));
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
