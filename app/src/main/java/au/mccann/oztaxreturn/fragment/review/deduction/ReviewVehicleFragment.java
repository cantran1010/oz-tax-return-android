package au.mccann.oztaxreturn.fragment.review.deduction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
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
import au.mccann.oztaxreturn.fragment.basic.OtherFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.DeductionResponse;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.model.Vehicles;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.FileUtils;
import au.mccann.oztaxreturn.utils.ImageUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.ExpandableLayout;
import au.mccann.oztaxreturn.view.MyGridView;
import au.mccann.oztaxreturn.view.RadioButtonCustom;
import au.mccann.oztaxreturn.view.TextViewCustom;
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
public class ReviewVehicleFragment extends BaseFragment implements View.OnClickListener {
    private RadioButtonCustom rbYes, rbNo;
    private EdittextCustom edtHow, edtKm, edtType, edtReg;
    private TextViewCustom edtAmount;
    private static final String TAG = OtherFragment.class.getSimpleName();
    private MyGridView grImage;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> images;
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private ExpandableLayout layout;
    private Vehicles vehicles = new Vehicles();
    private ArrayList<Attachment> attach;
    private int appID;
    private FloatingActionButton fab;
    private Float value = 0.0f;


    @Override
    protected int getLayout() {
        return R.layout.fragment_review_deduction_vehicle;
    }

    @Override
    protected void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        rbYes = (RadioButtonCustom) findViewById(R.id.rb_yes);
        rbYes.setEnabled(false);
        rbNo = (RadioButtonCustom) findViewById(R.id.rb_no);
        rbNo.setEnabled(false);
        edtHow = (EdittextCustom) findViewById(R.id.edt_how);
        edtHow.setEnabled(false);
        edtType = (EdittextCustom) findViewById(R.id.edt_type);
        edtType.setEnabled(false);
        edtKm = (EdittextCustom) findViewById(R.id.edt_km);
        edtKm.setEnabled(false);
        edtReg = (EdittextCustom) findViewById(R.id.edt_registration_number);
        edtReg.setEnabled(false);
        edtAmount = (TextViewCustom) findViewById(R.id.edt_calculated_amuont);
        edtAmount.setEnabled(false);
        grImage = (MyGridView) findViewById(R.id.gr_image);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        layout = (ExpandableLayout) findViewById(R.id.layout_expandable);
    }

    @Override
    protected void initData() {
        getReviewProgress(getApplicationResponse());
        images = new ArrayList<>();
        attach = new ArrayList<>();
        appID = getApplicationResponse().getId();
        if (isEditApp()) fab.setVisibility(View.VISIBLE);
        else fab.setVisibility(View.GONE);
        setTitle(getString(R.string.review_deductions));
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
        imageAdapter.setRemove(false);
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
        getReviewDeduction();
        edtKm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().isEmpty())
                    edtAmount.setText(Utils.displayCurrency(String.valueOf(value * Float.parseFloat(editable.toString().trim()))));
                else edtAmount.setText(Utils.displayCurrency(String.valueOf(0)));
            }
        });
    }


    private void updateUI(Vehicles b) {
        value = Float.parseFloat(b.getKmValue());
        rbYes.setChecked(b.isHad());
        edtHow.setText(b.getHowRelated());
        if (Integer.parseInt(b.getTravelled()) > 0) edtKm.setText(b.getTravelled());
        edtType.setText(b.getTypeBrand());
        edtReg.setText(b.getRegNumber());
        edtAmount.setText(Utils.displayCurrency(String.valueOf(Float.parseFloat(b.getKmValue()) * Float.parseFloat(b.getTravelled()))));
        showImage(b.getAttachments(), images, imageAdapter);
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

    private void getReviewDeduction() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        LogUtils.d(TAG, "getReviewDeduction appId : " + appID);
        ApiClient.getApiService().getReviewDeduction(UserManager.getUserToken(), appID).enqueue(new Callback<DeductionResponse>() {
            @Override
            public void onResponse(Call<DeductionResponse> call, Response<DeductionResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getReviewDeduction code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    vehicles = response.body().getVehicles();
                    if (vehicles != null) updateUI(vehicles);
                    LogUtils.d(TAG, "getReviewDeduction code : " + vehicles.toString());
                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getReviewDeduction error : " + error.message());
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<DeductionResponse> call, Throwable t) {
                LogUtils.e(TAG, "getReviewDeduction onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getReviewDeduction();
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
                govJson.put(Constants.PARAMETER_REVIEW_DEDUCTION_HOW, edtHow.getText().toString().trim());
                govJson.put(Constants.PARAMETER_REVIEW_DEDUCTION_KM, edtKm.getText().toString().trim());
                govJson.put(Constants.PARAMETER_REVIEW_DEDUCTION_TYPE, edtType.getText().toString().trim());
                govJson.put(Constants.PARAMETER_REVIEW_DEDUCTION_REG, edtReg.getText().toString().trim());
//                govJson.put(Constants.PARAMETER_REVIEW_AMOUNT, edtAmount.getF);
                if (images.size() > 1) {
                    for (Image image : images
                            ) {
                        if (image.getId() > 0) {
                            Attachment attachment = new Attachment();
                            attachment.setId((int) image.getId());
                            attachment.setUrl(image.getPath());
                            attach.add(attachment);
                        }
                    }
                    JSONArray jsonArray = new JSONArray();
                    for (Attachment mId : attach)
                        jsonArray.put(mId.getId());
                    govJson.put(Constants.PARAMETER_ATTACHMENTS, jsonArray);
                }
            }
            jsonRequest.put(Constants.PARAMETER_REVIEW_DEDUCTION_VEHICLES, govJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "doSaveReview jsonRequest : " + jsonRequest.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().putReviewDeduction(UserManager.getUserToken(), appID, body).enqueue(new Callback<DeductionResponse>() {
            @Override
            public void onResponse(Call<DeductionResponse> call, Response<DeductionResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                attach.clear();
                LogUtils.d(TAG, "doSaveReview code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
//                    LogUtils.d(TAG, "doSaveReview code: " + response.body().getClothes().toString());
                    openFragment(R.id.layout_container, ReviewClothesFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                }else if (response.code() == Constants.HTTP_CODE_BLOCK) {
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
            public void onFailure(Call<DeductionResponse> call, Throwable t) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                rbYes.setEnabled(true);
                rbNo.setEnabled(true);
                edtHow.setEnabled(true);
                edtHow.requestFocus();
                edtHow.setSelection(edtHow.length());
                edtKm.setEnabled(true);
                edtType.setEnabled(true);
                edtReg.setEnabled(true);
                edtAmount.setEnabled(true);
                imageAdapter.setRemove(isEditApp());
//               if (rbYes.isChecked())Utils.showSoftKeyboard(getContext(), edtHow);
                break;
            case R.id.btn_next:
                if (isEditApp()) {
                    if (rbYes.isChecked()) {
                        if (edtHow.getText().toString().trim().isEmpty()) {
                            edtHow.getParent().requestChildFocus(edtHow, edtHow);
                            showToolTip(getContext(), edtHow, getString(R.string.vali_all_empty));
                            return;
                        }
                        if (edtKm.getText().toString().trim().isEmpty()) {
                            edtKm.getParent().requestChildFocus(edtKm, edtKm);
                            showToolTip(getContext(), edtKm, getString(R.string.vali_all_empty));
                            return;
                        }
                        if (edtType.getText().toString().trim().isEmpty()) {
                            edtType.getParent().requestChildFocus(edtType, edtType);
                            showToolTip(getContext(), edtType, getString(R.string.vali_all_empty));
                            return;
                        }
                        if (edtReg.getText().toString().trim().isEmpty()) {
                            edtReg.getParent().requestChildFocus(edtReg, edtReg);
                            showToolTip(getContext(), edtReg, getString(R.string.vali_all_empty));
                            return;
                        }

                        if (images.size() < 2) {
                            showToolTip(getContext(), grImage, getString(R.string.vali_all_empty));
                            return;
                        }
                        uploadImage();
                    } else {
                        doSaveReview();
                    }
                } else
                    openFragment(R.id.layout_container, ReviewClothesFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;


        }

    }
}
