package au.mccann.oztaxreturn.fragment.basic;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.AlbumActivity;
import au.mccann.oztaxreturn.activity.PreviewImageActivity;
import au.mccann.oztaxreturn.activity.SplashActivity;
import au.mccann.oztaxreturn.adapter.ImageAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.dialog.PickImageDialog;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.model.ResponseBasicInformation;
import au.mccann.oztaxreturn.model.WagesSalary;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.utils.DateTimeUtils;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.FileUtils;
import au.mccann.oztaxreturn.utils.ImageUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.ButtonCustom;
import au.mccann.oztaxreturn.view.CheckBoxCustom;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.MyGridView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static au.mccann.oztaxreturn.utils.DateTimeUtils.getDateBirthDayFromIso;
import static au.mccann.oztaxreturn.utils.ImageUtils.showImage;
import static au.mccann.oztaxreturn.utils.Utils.showToolTip;

/**
 * Created by LongBui on 4/17/18.
 */

public class WagesSalaryFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = WagesSalaryFragment.class.getSimpleName();
    private MyGridView grImage;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> images;
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private CheckBoxCustom cbYes, cbNo;
    private LinearLayout layoutYes, layoutNo;
    private ButtonCustom btnNext;
    private EdittextCustom edtTfn, edtFirstName, edtMidName, edtLastName;
    private EdittextCustom edtBirthday;
    private Calendar calendar = GregorianCalendar.getInstance();
    private ArrayList<Attachment> attach;

    @Override
    protected int getLayout() {
        return R.layout.fragment_income_wages_salary;
    }

    @Override
    protected void initView() {
        grImage = (MyGridView) findViewById(R.id.gr_image);
        cbYes = (CheckBoxCustom) findViewById(R.id.cb_payg_yes);
        cbNo = (CheckBoxCustom) findViewById(R.id.cb_payg_no);
        edtBirthday = (EdittextCustom) findViewById(R.id.tv_birthday);
        layoutYes = (LinearLayout) findViewById(R.id.layout_yes);
        layoutNo = (LinearLayout) findViewById(R.id.layout_no);
        btnNext = (ButtonCustom) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        edtTfn = (EdittextCustom) findViewById(R.id.edt_tfn);
        edtFirstName = (EdittextCustom) findViewById(R.id.edt_first_name);
        edtMidName = (EdittextCustom) findViewById(R.id.edt_middle_name);
        edtLastName = (EdittextCustom) findViewById(R.id.edt_last_name);
        edtBirthday.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        images = new ArrayList<>();
        attach = new ArrayList<>();
        setTitle(getString(R.string.income_ws_title));
        appBarVisibility(false, true, 0);
        //images
        if (images.size() == 0) {
            final Image image = new Image();
            image.setId(0);
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
        getBasicInformation();
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
                    if (response.body().getIncomeWagesSalary() != null)
                        updateUI(response.body().getIncomeWagesSalary());
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

    private void updateUI(WagesSalary salary) {
        if (salary.getAttachments() != null && salary.getAttachments().size() > 0) {
            cbYes.setChecked(true);
            showImage(salary.getAttachments(), images, imageAdapter);
        } else {
            cbNo.setChecked(true);
            edtTfn.setText(salary.getTfnNumber());
            edtFirstName.setText(salary.getFirstName());
            edtMidName.setText(salary.getLastName());
            edtLastName.setText(salary.getLastName());
            if (!TextUtils.isEmpty(salary.getBirthday()))
                edtBirthday.setText(getDateBirthDayFromIso(salary.getBirthday()));
        }

    }

    private void uploadImage() {
        final ArrayList<Image> listUp = new ArrayList<>();
        for (Image image : images
                ) {
            if (image.getId() == 0 && !image.isAdd()) listUp.add(image);
        }
        if (listUp.size() > 0) {
            LogUtils.d(TAG, "uploadImage" + listUp.toString());
            ImageUtils.doUploadImage(getContext(), listUp, new ImageUtils.UpImagesListener() {
                @Override
                public void onSuccess(List<Attachment> responses) {
                    attach.addAll(responses);
                    doSaveBasic();
                }
            });
        } else {
            doSaveBasic();
        }


    }

    private void doSaveBasic() {
        LogUtils.d(TAG, "doSaveBasic" + cbYes.isChecked());
        ProgressDialogUtils.showProgressDialog(getContext());
        JSONObject jsonRequest = new JSONObject();
        try {
            JSONObject salaryJson = new JSONObject();
            if (cbYes.isChecked()) {
                for (Image image : images
                        ) {
                    if (image.getId() > 0) {
                        Attachment attachment = new Attachment();
                        attachment.setId((int) image.getId());
                        attachment.setUrl(image.getPath());
                        if (!attach.contains(attachment))
                            attach.add(attachment);
                    }
                }
                JSONArray jsonArray = new JSONArray();
                for (Attachment mId : attach)
                    jsonArray.put(mId.getId());
                salaryJson.put("attachments", jsonArray);
            } else if (cbNo.isChecked()) {
                salaryJson.put(Constants.PARAMETER_INCOME_TFN, edtTfn.getText().toString().trim());
                salaryJson.put(Constants.PARAMETER_INCOME_FIRST_NAME, edtFirstName.getText().toString().trim());
                salaryJson.put(Constants.PARAMETER_INCOME_MID_NAME, edtMidName.getText().toString().trim());
                salaryJson.put(Constants.PARAMETER_INCOME_LAST_NAME, edtLastName.getText().toString().trim());
                salaryJson.put(Constants.PARAMETER_INCOME_BIRTH_DAY, DateTimeUtils.fromCalendarToBirthday(calendar));

            }
            jsonRequest.put("income_wages_salary", salaryJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "doSaveBasic jsonRequest : " + jsonRequest.toString());
        LogUtils.d(TAG, "doSaveBasic appId : " + getApplicationResponse().getId());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().saveBasicInformation(UserManager.getUserToken(), getApplicationResponse().getId(), body).enqueue(new Callback<ResponseBasicInformation>() {
            @Override
            public void onResponse(Call<ResponseBasicInformation> call, Response<ResponseBasicInformation> response) {
                ProgressDialogUtils.dismissProgressDialog();
                attach.clear();
                LogUtils.d(TAG, "doSaveBasic code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doSaveBasic body: " + response.body());
                    openFragment(R.id.layout_container, OtherFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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
            requestPermissions(permissions, Constants.PERMISSION_REQUEST_CODE);
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
        File file = new File(FileUtils.getInstance().getMaximyzDirectory(), "image" + System.currentTimeMillis() + ".jpg");
        Uri imgUri = Uri.fromFile(file);
        this.imgPath = file.getAbsolutePath();
        return imgUri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (cameraPermission && readExternalFile) {
                permissionGrantedImageAttach();
            }
        }
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
            image.setId(0);
            image.setAdd(false);
            image.setPath(selectedImagePath);
            images.add(0, image);
            imageAdapter.notifyDataSetChanged();
        }
    }

    private String getImagePath() {
        return imgPath;
    }


    private void openDatePicker() {
        @SuppressWarnings("deprecation") DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        if (view.isShown()) {
                            calendar.set(year, monthOfYear, dayOfMonth);
//                            edtBirthday.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));
                            edtBirthday.setText(DateTimeUtils.fromCalendarToView(calendar));

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
                LogUtils.d(TAG, "doSaveBasic" + cbYes.isChecked() + images.size());
                if (cbYes.isChecked()) {
                    if (images.size() < 2) {
                        showToolTip(getContext(), grImage, getString(R.string.err_pick_image));
                        return;
                    } else uploadImage();
                } else if (cbNo.isChecked()) {
                    if (edtTfn.getText().toString().trim().isEmpty()) {
                        showToolTip(getActivity(), edtTfn, getString(R.string.vali_all_empty));
                        return;
                    }
                    if (edtFirstName.getText().toString().trim().isEmpty()) {
                        showToolTip(getActivity(), edtFirstName, getString(R.string.vali_all_empty));
                        return;
                    }
                    if (edtMidName.getText().toString().trim().isEmpty()) {
                        showToolTip(getActivity(), edtMidName, getString(R.string.vali_all_empty));
                        return;
                    }
                    if (edtLastName.getText().toString().trim().isEmpty()) {
                        showToolTip(getActivity(), edtLastName, getString(R.string.vali_all_empty));
                        return;
                    }
                    if (edtBirthday.getText().toString().trim().isEmpty()) {
                        showToolTip(getActivity(), edtBirthday, getString(R.string.vali_all_empty));
                        return;
                    }
                    doSaveBasic();
                } else {
                    showToolTip(getActivity(), btnNext, getString(R.string.error_must_one));
                }
                break;

            case R.id.tv_birthday:
                openDatePicker();
                break;
        }
    }
}
