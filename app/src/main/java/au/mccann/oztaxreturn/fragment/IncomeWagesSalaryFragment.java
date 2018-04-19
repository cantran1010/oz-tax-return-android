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
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.dialog.PickImageDialog;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.model.ImageResponse;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.utils.DateTimeUtils;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.FileUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.ButtonCustom;
import au.mccann.oztaxreturn.view.CheckBoxCustom;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.MyGridView;
import au.mccann.oztaxreturn.view.TextViewCustom;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 4/17/18.
 */

public class IncomeWagesSalaryFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = IncomeWagesSalaryFragment.class.getSimpleName();
    private MyGridView grImage;
    private ImageAdapter imageAdapter;
    private final ArrayList<Image> images = new ArrayList<>();
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath, imgPathBackground;
    private CheckBoxCustom cbYes, cbNo;
    private LinearLayout layoutYes, layoutNo;
    private ButtonCustom btnNext;
    private EdittextCustom edtTfn, edtFirstName, edtMidName, edtLastName;
    private TextViewCustom tvBirthday;
    private Calendar calendar = GregorianCalendar.getInstance();

    @Override
    protected int getLayout() {
        return R.layout.fragment_income_wages_salary;
    }

    @Override
    protected void initView() {
        grImage = (MyGridView) findViewById(R.id.gr_image);
        cbYes = (CheckBoxCustom) findViewById(R.id.cb_payg_yes);
        cbNo = (CheckBoxCustom) findViewById(R.id.cb_payg_no);

        layoutYes = (LinearLayout) findViewById(R.id.layout_yes);
        layoutNo = (LinearLayout) findViewById(R.id.layout_no);

        btnNext = (ButtonCustom) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);

        edtTfn = (EdittextCustom) findViewById(R.id.edt_tfn);
        edtFirstName = (EdittextCustom) findViewById(R.id.edt_first_name);
        edtMidName = (EdittextCustom) findViewById(R.id.edt_middle_name);
        edtLastName = (EdittextCustom) findViewById(R.id.edt_last_name);

        tvBirthday = (TextViewCustom) findViewById(R.id.tv_birthday);
        tvBirthday.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.income_ws_title));
        appBarVisibility(false, true);

        //images
        final Image image = new Image();
        image.setAdd(true);
        images.add(image);

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
        Bundle bundle = new Bundle();

        if (cbYes.isChecked()) {
            doUploadImage();
        } else if (cbNo.isChecked()) {
            bundle.putString("income_tfn_number", edtTfn.getText().toString());
            bundle.putString("income_first_name", edtFirstName.getText().toString());
            bundle.putString("income_middle_name", edtMidName.getText().toString());
            bundle.putString("income_last_name", edtLastName.getText().toString());
            bundle.putString("birthday", DateTimeUtils.fromCalendarToBirthday(calendar));

            LogUtils.d(TAG, "doNext , bundle : " + bundle.toString());
        }

        openFragment(R.id.layout_container, IncomeOther.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);

    }

    private void doUploadImage() {
        ProgressDialogUtils.showProgressDialog(getActivity());
//        File fileUp = Utils.compressFile(fileAttach);
//        LogUtils.d(TAG, "doAttachImage , file Name : " + fileUp.getName());
//        final RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), fileUp);
//        MultipartBody.Part itemPart = MultipartBody.Part.createFormData("image", fileUp.getName(), requestBody);

        List<MultipartBody.Part> parts = new ArrayList<>();
        // last image is "plus attach" , so realy size = size -1
        for (int i = 0; i < images.size() - 1; i++)
            parts.add(MultipartBody.Part.createFormData("images[]", images.get(i).getName(), RequestBody.create(MediaType.parse("image/*"), new File(images.get(i).getPath()))));

        ApiClient.getApiService().uploadImage(UserManager.getUserToken(), parts).enqueue(new Callback<List<ImageResponse>>() {
            @Override
            public void onResponse(Call<List<ImageResponse>> call, Response<List<ImageResponse>> response) {
                LogUtils.d(TAG, "doUploadImage onResponse : " + response.body());
                LogUtils.d(TAG, "doUploadImage code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {

                    int[] imageArrIds = new int[response.body().size()];
                    for (int i = 0; i < response.body().size(); i++)
                        imageArrIds[i] = response.body().get(i).getId();

                    Bundle bundle = new Bundle();
                    bundle.putIntArray("income_wage_attachments", imageArrIds);

                    LogUtils.d(TAG, "doNext , bundle : " + bundle);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "doUploadImage error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }
                FileUtils.deleteDirectory(new File(FileUtils.OUTPUT_DIR));
                ProgressDialogUtils.dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<List<ImageResponse>> call, Throwable t) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.e(TAG, "doUploadImage onFailure : " + t.getMessage());
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doUploadImage();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                ProgressDialogUtils.dismissProgressDialog();
                FileUtils.deleteDirectory(new File(FileUtils.OUTPUT_DIR));
            }
        });
    }

    private void openDatePicker() {
        @SuppressWarnings("deprecation") DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        if (view.isShown()) {
                            calendar.set(year, monthOfYear, dayOfMonth);
                            String strDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            tvBirthday.setText(strDate);
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//        Calendar calendarMax = Calendar.getInstance();
//        // Set calendar to 1 day next from today
//        calendarMax.add(Calendar.DAY_OF_MONTH, 1);
//        // Set calendar to 1 month next
//        calendarMax.add(Calendar.MONTH, 1);
//        datePickerDialog.getDatePicker().setMinDate(new Date().getTime() - 10000);
//        datePickerDialog.getDatePicker().setMaxDate(calendarMax.getTimeInMillis());

        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime() - 10000);

        datePickerDialog.setTitle(getString(R.string.your_birthday));
        datePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:

                if (!cbYes.isChecked() && !cbNo.isChecked())
                    Utils.showLongToast(getActivity(), getString(R.string.error_must_one), true, false);
                else
                    doNext();
                break;

            case R.id.tv_birthday:
                openDatePicker();
                break;
        }
    }
}
