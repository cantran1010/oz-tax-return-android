package au.mccann.oztaxreturn.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Spinner;

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
import au.mccann.oztaxreturn.utils.DateTimeUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.RadioButtonCustom;

import static au.mccann.oztaxreturn.utils.TooltipUtils.showToolTipView;

/**
 * Created by CanTran on 4/19/18.
 */
public class PersonalInformation extends BaseFragment implements View.OnClickListener {
    private static final String TAG = PersonalInformation.class.getSimpleName();
    private EdittextCustom edtTitle, edtFirstName, edtMidName, edtLastName, edtBirthDay;
    private Spinner spGender;
    private RadioButtonCustom rbYes;
    private Calendar calendar = GregorianCalendar.getInstance();
    private List<String> genders = new ArrayList<>();
    private Bundle bundle = new Bundle();

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
        bundle = getArguments();
        LogUtils.d(TAG, "initData bundle : " + bundle.toString());
        genders = Arrays.asList(getResources().getStringArray(R.array.string_array_gender));
        OzSpinnerAdapter dataNameAdapter = new OzSpinnerAdapter(getContext(), genders);
        spGender.setAdapter(dataNameAdapter);
        findViewById(R.id.btn_next).setOnClickListener(this);
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
        bundle.putString(Constants.PARAMETER_APP_TITLE, edtTitle.getText().toString().trim());
        bundle.putString(Constants.PARAMETER_APP_FIRST_NAME, edtFirstName.getText().toString().trim());
        bundle.putString(Constants.PARAMETER_APP_MID_NAME, edtMidName.getText().toString().trim());
        bundle.putString(Constants.PARAMETER_APP_LAST_NAME, edtLastName.getText().toString().trim());
        bundle.putString(Constants.PARAMETER_APP_BIRTH_DAY, DateTimeUtils.fromCalendarToBirthday(calendar));
        bundle.putString(Constants.PARAMETER_APP_GENDER, spGender.getSelectedItem().toString());
        bundle.putBoolean(Constants.PARAMETER_APP_LOCAL, rbYes.isChecked());
        openFragment(R.id.layout_container, SubmitInformation.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
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
