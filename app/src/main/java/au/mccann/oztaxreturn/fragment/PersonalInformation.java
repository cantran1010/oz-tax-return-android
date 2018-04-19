package au.mccann.oztaxreturn.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
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
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.RadioButtonCustom;
import au.mccann.oztaxreturn.view.TextViewCustom;

/**
 * Created by CanTran on 4/19/18.
 */
public class PersonalInformation extends BaseFragment implements View.OnClickListener {
    private EdittextCustom edtTitle, edtFirstName, edtMidName, edtLastName;
    private TextViewCustom tvBirthDay;
    private Spinner spGender;
    private RelativeLayout layoutGender;
    private RadioButtonCustom rbYes;
    private Calendar calendar = GregorianCalendar.getInstance();
    private List<String> genders = new ArrayList<>();
    ;

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
        tvBirthDay = (TextViewCustom) findViewById(R.id.tv_birthday);
        spGender = (Spinner) findViewById(R.id.sp_gender);
        rbYes = (RadioButtonCustom) findViewById(R.id.rb_yes);
        layoutGender = (RelativeLayout) findViewById(R.id.layout_gender);
        tvBirthDay.setOnClickListener(this);


    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.personal_information_title));
        appBarVisibility(false, true);
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
                            tvBirthDay.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_birthday:
                openDatePicker();
                break;
            case R.id.btn_next:
                openFragment(R.id.layout_container, SubmitInformation.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;

        }

    }
}
