package au.mccann.oztaxreturn.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.AlbumActivity;
import au.mccann.oztaxreturn.activity.PreviewImageActivity;
import au.mccann.oztaxreturn.adapter.ImageAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.dialog.PickImageDialog;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.model.ImageResponse;
import au.mccann.oztaxreturn.utils.DateTimeUtils;
import au.mccann.oztaxreturn.utils.FileUtils;
import au.mccann.oztaxreturn.utils.ImageUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.ButtonCustom;
import au.mccann.oztaxreturn.view.CheckBoxCustom;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.MyGridView;

import static au.mccann.oztaxreturn.utils.TooltipUtils.showToolTipView;

/**
 * Created by LongBui on 4/17/18.
 */

public class IncomeWagesSalaryFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = IncomeWagesSalaryFragment.class.getSimpleName();
    private MyGridView grImage;
    private ImageAdapter imageAdapter;
    private final ArrayList<Image> images = new ArrayList<>();
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private CheckBoxCustom cbYes, cbNo;
    private LinearLayout layoutYes, layoutNo;
    private ButtonCustom btnNext;
    private EdittextCustom edtTfn, edtFirstName, edtMidName, edtLastName;
    private EdittextCustom tvBirthday;
    private Calendar calendar = GregorianCalendar.getInstance();
    private Bundle bundle = new Bundle();

    @Override
    protected int getLayout() {
        return R.layout.fragment_income_wages_salary;
    }

    @Override
    protected void initView() {
        grImage = (MyGridView) findViewById(R.id.gr_image);
        cbYes = (CheckBoxCustom) findViewById(R.id.cb_payg_yes);
        cbNo = (CheckBoxCustom) findViewById(R.id.cb_payg_no);
        tvBirthday = (EdittextCustom) findViewById(R.id.tv_birthday);
        layoutYes = (LinearLayout) findViewById(R.id.layout_yes);
        layoutNo = (LinearLayout) findViewById(R.id.layout_no);

        btnNext = (ButtonCustom) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);

        edtTfn = (EdittextCustom) findViewById(R.id.edt_tfn);
        edtFirstName = (EdittextCustom) findViewById(R.id.edt_first_name);
        edtMidName = (EdittextCustom) findViewById(R.id.edt_middle_name);
        edtLastName = (EdittextCustom) findViewById(R.id.edt_last_name);


        tvBirthday.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.income_ws_title));
        appBarVisibility(false, true,0);
        bundle = getArguments();
        LogUtils.d(TAG, "initData bundle : " + bundle.toString());
        //images
        if (images.size() == 0) {
            final Image image = new Image();
            image.setAdd(true);
            images.add(image);
        }
        imageAdapter = new ImageAdapter(getActivity(), images);
        grImage.setAdapter(imageAdapter);

        grImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (images.get(position).isAdd) {
                    if (images.size() >= 10) {
                        Utils.showLongToast(getActivity(), getString(R.string.max_image_attach_err, 9), true, false);
                    } else {
                        checkPermissionImageAttach();
                    }
                } else {
                    Intent intent = new Intent(getActivity(), PreviewImageActivity.class);
                    intent.putExtra(Constants.EXTRA_IMAGE_PATH, images.get(position).getPath());
                    startActivity(intent, TransitionScreen.RIGHT_TO_LEFT);
                }
            }
        });

        cbYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtils.d(TAG, "setOnCheckedChangeListener : " + b);
                if (b) {
                    cbNo.setChecked(false);
                    layoutYes.setVisibility(View.VISIBLE);
                    layoutNo.setVisibility(View.GONE);
                } else {
                    layoutYes.setVisibility(View.GONE);
                    layoutNo.setVisibility(View.GONE);
                }
            }
        });

        cbNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtils.d(TAG, "setOnCheckedChangeListener : " + b);
                if (b) {
                    cbYes.setChecked(false);
                    layoutNo.setVisibility(View.VISIBLE);
                    layoutYes.setVisibility(View.GONE);
                } else {
                    layoutYes.setVisibility(View.GONE);
                    layoutNo.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    private void checkPermissionImageAttach() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) + ContextCompat
                .checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, Constants.PERMISSION_REQUEST_CODE);
        } else {
            permissionGrantedImageAttach();
        }
    }

    private void permissionGrantedImageAttach() {
        PickImageDialog pickImageDialog = new PickImageDialog(getActivity());
        pickImageDialog.setPickImageListener(new PickImageDialog.PickImageListener() {
            @Override
            public void onCamera() {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                startActivityForResult(cameraIntent, Constants.REQUEST_CODE_CAMERA);
            }

            @Override
            public void onGallery() {
                Intent intent = new Intent(getActivity(), AlbumActivity.class);
                intent.putExtra(Constants.COUNT_IMAGE_ATTACH_EXTRA, images.size() - 1);
                startActivityForResult(intent, Constants.REQUEST_CODE_PICK_IMAGE, TransitionScreen.RIGHT_TO_LEFT);
            }
        });
        pickImageDialog.showView();
    }

    private Uri setImageUri() {
        File file = new File(FileUtils.getInstance().getHozoDirectory(), "image" + System.currentTimeMillis() + ".jpg");
        Uri imgUri = Uri.fromFile(file);
        this.imgPath = file.getAbsolutePath();
        return imgUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // image attach
        if (requestCode == Constants.REQUEST_CODE_PICK_IMAGE
                && resultCode == Constants.RESPONSE_CODE_PICK_IMAGE
                && data != null) {
            ArrayList<Image> imagesSelected = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            images.addAll(0, imagesSelected);
            imageAdapter.notifyDataSetChanged();
        } else if (requestCode == Constants.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            final String selectedImagePath = getImagePath();
            LogUtils.d(TAG, "onActivityResult selectedImagePath : " + selectedImagePath);
            Image image = new Image();
            image.setAdd(false);
            image.setPath(selectedImagePath);
            images.add(0, image);
            imageAdapter.notifyDataSetChanged();
        }
    }

    private String getImagePath() {
        return imgPath;
    }

    private void doNext() {
        if (cbYes.isChecked()) {
            if (images.size() < 2) {
                showToolTipView(getContext(), grImage, Gravity.TOP, getString(R.string.valid_deduction_image), ContextCompat.getColor(getContext(), R.color.red));
                return;
            }
            ImageUtils.doUploadImage(getContext(), images, new ImageUtils.UpImagesListener() {
                @Override
                public void onSuccess(List<ImageResponse> responses) {

                    int[] imageArrIds = new int[responses.size()];
                    for (int i = 0; i < responses.size(); i++)
                        imageArrIds[i] = responses.get(i).getId();
                    bundle.putIntArray(Constants.PARAMETER_WAGE_ATTACHMENTS, imageArrIds);
                    LogUtils.d(TAG, "doNext , bundle : " + bundle);
                    openFragment(R.id.layout_container, IncomeOther.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
                }
            });
        } else if (cbNo.isChecked()) {
            if (edtTfn.getText().toString().trim().isEmpty()) {
                showToolTipView(getContext(), edtTfn, Gravity.BOTTOM, getString(R.string.valid_tfn),
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
            if (tvBirthday.getText().toString().trim().isEmpty()) {
                showToolTipView(getContext(), tvBirthday, Gravity.TOP, getString(R.string.valid_birth_day),
                        ContextCompat.getColor(getContext(), R.color.red));
                return;
            }
            bundle.putString(Constants.PARAMETER_INCOME_TFN, edtTfn.getText().toString().trim());
            bundle.putString(Constants.PARAMETER_INCOME_FIRST_NAME, edtFirstName.getText().toString().trim());
            bundle.putString(Constants.PARAMETER_INCOME_MID_NAME, edtMidName.getText().toString().trim());
            bundle.putString(Constants.PARAMETER_INCOME_LAST_NAME, edtLastName.getText().toString().trim());
            bundle.putString(Constants.PARAMETER_INCOME_BIRTH_DAY, DateTimeUtils.fromCalendarToBirthday(calendar));
            LogUtils.d(TAG, "doNext , bundle : " + bundle.toString());
            openFragment(R.id.layout_container, IncomeOther.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
        }


    }

    private void openDatePicker() {
        @SuppressWarnings("deprecation") DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        if (view.isShown()) {
                            calendar.set(year, monthOfYear, dayOfMonth);
                            tvBirthday.setText(DateTimeUtils.fromCalendarToBirthday(calendar));
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime() - 10000);
        datePickerDialog.setTitle(getString(R.string.your_birthday));
        datePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (!cbYes.isChecked() && !cbNo.isChecked())
                    showToolTipView(getContext(), btnNext, Gravity.TOP, getString(R.string.error_must_one),
                            ContextCompat.getColor(getContext(), R.color.red));
                else
                    doNext();
                break;

            case R.id.tv_birthday:
                openDatePicker();
                break;
        }
    }
}
