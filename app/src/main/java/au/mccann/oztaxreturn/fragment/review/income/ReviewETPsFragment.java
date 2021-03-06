package au.mccann.oztaxreturn.fragment.review.income;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.AlbumActivity;
import au.mccann.oztaxreturn.activity.HomeActivity;
import au.mccann.oztaxreturn.activity.PreviewImageActivity;
import au.mccann.oztaxreturn.activity.SplashActivity;
import au.mccann.oztaxreturn.adapter.ImageAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.dialog.CodeDialog;
import au.mccann.oztaxreturn.dialog.PickImageDialog;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.fragment.basic.OtherFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Etps;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.model.IncomeResponse;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.utils.DateTimeUtils;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.FileUtils;
import au.mccann.oztaxreturn.utils.ImageUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.EditTextEasyMoney;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.ExpandableLayout;
import au.mccann.oztaxreturn.view.MyGridView;
import au.mccann.oztaxreturn.view.RadioButtonCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static au.mccann.oztaxreturn.utils.ImageUtils.showImage;
import static au.mccann.oztaxreturn.utils.Utils.showToolTip;


/**
 * Created by CanTran on 4/24/18.
 */
public class ReviewETPsFragment extends BaseFragment implements View.OnClickListener {
    private RadioButtonCustom rbYes, rbNo;
    private EdittextCustom edtPaymentDate, edtPayerAbn, edtCode;
    private EditTextEasyMoney edtTaxWidthheld, edtTaxtableComponent;
    private static final String TAG = OtherFragment.class.getSimpleName();
    private MyGridView grImage;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> images;
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private ExpandableLayout layout;
    private Etps etps = new Etps();
    private ArrayList<Attachment> attach;
    private int appID;
    private final Calendar calendar = GregorianCalendar.getInstance();

    @Override
    protected int getLayout() {
        return R.layout.fragment_early_termination_payments;
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_next).setOnClickListener(this);
        rbYes = (RadioButtonCustom) findViewById(R.id.rb_yes);
        rbNo = (RadioButtonCustom) findViewById(R.id.rb_no);
        edtPaymentDate = (EdittextCustom) findViewById(R.id.edt_payment_date);
        edtPaymentDate.setOnClickListener(this);
        edtPayerAbn = (EdittextCustom) findViewById(R.id.edt_payer_abn);
        edtTaxWidthheld = (EditTextEasyMoney) findViewById(R.id.edt_tax_widthheld);
        edtTaxtableComponent = (EditTextEasyMoney) findViewById(R.id.edt_taxtable_component);
        edtCode = (EdittextCustom) findViewById(R.id.edt_code);
        edtCode.setOnClickListener(this);
        grImage = (MyGridView) findViewById(R.id.gr_image);
        layout = (ExpandableLayout) findViewById(R.id.layout_expandable);


    }

    @Override
    protected void initData() {
        ((HomeActivity) getActivity()).setIndex(16);
        rbYes.setEnabled(isEditApp());
        rbNo.setEnabled(isEditApp());
        edtPaymentDate.setEnabled(isEditApp());
        edtPayerAbn.setEnabled(isEditApp());
        edtTaxWidthheld.setEnabled(isEditApp());
        edtTaxtableComponent.setEnabled(isEditApp());
        edtCode.setEnabled(isEditApp());
        getReviewProgress(getApplicationResponse());
        images = new ArrayList<>();
        attach = new ArrayList<>();
        appID = getApplicationResponse().getId();
        setTitle(getString(R.string.review_income_title));
        appBarVisibility(true, true, 1);
        //images
        if (images.size() == 0) {
            final Image image = new Image();
            image.setId(0);
            image.setAdd(true);
            images.add(image);
        }
        imageAdapter = new ImageAdapter(getActivity(), images);
        grImage.setAdapter(imageAdapter);
        imageAdapter.setRemove(isEditApp());
        grImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (images.get(position).isAdd) {
                    if (!imageAdapter.isRemove()) return;
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
        rbYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtils.d(TAG, "setOnCheckedChangeListener : " + b);
                if (b) {
                    layout.setExpanded(true);
//                    scollLayout();
                } else {
                    layout.setExpanded(false);
                }
            }
        });
        getReviewIncome();
    }


    private void updateUI(Etps e) {
        rbYes.setChecked(e.isHad());
        if (e.getPaymentDate() != null && !e.getPaymentDate().isEmpty())
            try {
                edtPaymentDate.setText(DateTimeUtils.fromCalendarToView(DateTimeUtils.toCalendarBirthday(e.getPaymentDate())));
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        edtPayerAbn.setText(e.getPayerAbn());
        edtTaxWidthheld.setText(e.getTaxWithheld());
        edtTaxtableComponent.setText(e.getTaxableCom());
        edtCode.setText(e.getCode());
        showImage(e.getAttachments(), images, imageAdapter);
    }

    private void checkPermissionImageAttach() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) + ContextCompat
                .checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, Constants.PERMISSION_REQUEST_CODE);
        } else {
            permissionGrantedImageAttach();
        }
    }

    private void permissionGrantedImageAttach() {
        PickImageDialog pickImageDialog = new PickImageDialog(getContext());
        pickImageDialog.setPickImageListener(new PickImageDialog.PickImageListener() {
            @Override
            public void onCamera() {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    private void getReviewIncome() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        LogUtils.d(TAG, "getReviewIncome code : " + appID);
        ApiClient.getApiService().getReviewIncome(UserManager.getUserToken(), appID).enqueue(new Callback<IncomeResponse>() {
            @Override
            public void onResponse(Call<IncomeResponse> call, Response<IncomeResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getReviewIncome code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getReviewIncome code : " + response.body().getEtps().toString());
                    etps = response.body().getEtps();
                    if (etps != null) updateUI(etps);
                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getReviewIncome error : " + error.message());
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<IncomeResponse> call, Throwable t) {
                LogUtils.e(TAG, "getReviewIncome onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getReviewIncome();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
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
                    FileUtils.deleteDirectory(new File(FileUtils.OUTPUT_DIR));
                    doSaveReview();
                }
            });
        } else {
            doSaveReview();
        }


    }

    private void doSaveReview() {
        ProgressDialogUtils.showProgressDialog(getContext());
        final JSONObject jsonRequest = new JSONObject();
        try {
            JSONObject govJson = new JSONObject();
            govJson.put(Constants.PARAMETER_REVIEW_HAD, rbYes.isChecked());
            if (rbYes.isChecked()) {
                govJson.put(Constants.PARAMETER_REVIEW_ETPS_PAYMENT_DATE, DateTimeUtils.fromCalendarToBirthday(calendar));
                govJson.put(Constants.PARAMETER_REVIEW_ETPS_PAYMENT_ABN, edtPayerAbn.getText().toString().trim());
                govJson.put(Constants.PARAMETER_REVIEW_ETPS_PAYMENT_TAX, edtTaxWidthheld.getValuesFloat());
                govJson.put(Constants.PARAMETER_REVIEW_ETPS_PAYMENT_COM, edtTaxtableComponent.getValuesFloat());
                govJson.put(Constants.PARAMETER_REVIEW_ETPS_PAYMENT_CODE, edtCode.getText().toString().trim());
                if (images.size() > 1) {
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
                    govJson.put(Constants.PARAMETER_ATTACHMENTS, jsonArray);
                }
            }
            jsonRequest.put(Constants.PARAMETER_REVIEW_INCOME_ETPS, govJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "doSaveReview jsonRequest : " + jsonRequest.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().putReviewIncom(UserManager.getUserToken(), appID, body).enqueue(new Callback<IncomeResponse>() {
            @Override
            public void onResponse(Call<IncomeResponse> call, Response<IncomeResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                attach.clear();
                LogUtils.d(TAG, "doSaveReview code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doSaveReview code: " + response.body().getJobs().toString());
                    openFragment(R.id.layout_container, ReviewAnnuitiesFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.e(TAG, "doSaveReview error : " + error.message());
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
            public void onFailure(Call<IncomeResponse> call, Throwable t) {
                LogUtils.e(TAG, "doSaveReview onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getContext(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doSaveReview();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
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
//                            edtPaymentDate.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));
                            edtPaymentDate.setText(DateTimeUtils.fromCalendarToView(calendar));
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime() - 10000);
        datePickerDialog.setTitle(getString(R.string.your_birthday));
        datePickerDialog.show();
    }

    private void selectCode() {
        CodeDialog codeDialog = new CodeDialog(getContext());
        codeDialog.setValues(edtCode.getText().toString());
        codeDialog.setCodeListenner(new CodeDialog.CodeListenner() {
            @Override
            public void onCodeListenner(String values) {
                edtCode.setText(values);
            }
        });
        codeDialog.showView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edt_payment_date:
                openDatePicker();
                break;
            case R.id.edt_code:
                selectCode();
                break;
            case R.id.btn_next:
                if (isEditApp()) {
                    if (rbYes.isChecked()) {
                        if (edtPaymentDate.getText().toString().trim().isEmpty()) {
                            edtPaymentDate.getParent().requestChildFocus(edtPaymentDate, edtPaymentDate);
                            showToolTip(getContext(), edtPaymentDate, getString(R.string.vali_all_empty));
                            return;
                        }
                        if (edtPayerAbn.getText().toString().trim().isEmpty()) {
                            edtPayerAbn.findFocus();
                            edtPayerAbn.getParent().requestChildFocus(edtPayerAbn, edtPayerAbn);
                            showToolTip(getContext(), edtPayerAbn, getString(R.string.vali_all_empty));
                            return;
                        }
                        if (edtTaxWidthheld.getText().toString().trim().isEmpty()) {
                            edtTaxWidthheld.findFocus();
                            edtTaxWidthheld.getParent().requestChildFocus(edtTaxWidthheld, edtTaxWidthheld);
                            showToolTip(getContext(), edtTaxWidthheld, getString(R.string.vali_all_empty));
                            return;
                        }
                        if (edtTaxtableComponent.getText().toString().trim().isEmpty()) {
                            edtTaxtableComponent.findFocus();
                            edtTaxtableComponent.getParent().requestChildFocus(edtTaxtableComponent, edtTaxtableComponent);
                            showToolTip(getContext(), edtTaxtableComponent, getString(R.string.vali_all_empty));
                            return;
                        }
                        if (edtCode.getText().toString().trim().isEmpty()) {
                            edtCode.findFocus();
                            edtCode.getParent().requestChildFocus(edtCode, edtCode);
                            showToolTip(getContext(), edtCode, getString(R.string.vali_all_empty));
                            return;
                        }
//                        if (images.size() < 2) {
//                            grImage.getParent().requestChildFocus(grImage, grImage);
//                            showToolTip(getContext(), grImage, getString(R.string.valid_deduction_image));
//                            return;
//                        }
                        uploadImage();

                    } else {
                        doSaveReview();
                    }
                } else
                    openFragment(R.id.layout_container, ReviewAnnuitiesFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;


        }

    }


}
