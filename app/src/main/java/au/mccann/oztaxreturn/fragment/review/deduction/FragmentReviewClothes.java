package au.mccann.oztaxreturn.fragment.review.deduction;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
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
import au.mccann.oztaxreturn.adapter.ImageAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.dialog.PickImageDialog;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.fragment.basic.IncomeOther;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Clothes;
import au.mccann.oztaxreturn.model.DeductionResponse;
import au.mccann.oztaxreturn.model.Image;
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
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static au.mccann.oztaxreturn.utils.ImageUtils.showImage;
import static au.mccann.oztaxreturn.utils.TooltipUtils.showToolTipView;

/**
 * Created by CanTran on 4/24/18.
 */
public class FragmentReviewClothes extends BaseFragment implements View.OnClickListener {
    private RadioButtonCustom rbYes, rbNo;
    private EdittextCustom edtType, edtAmount;
    private static final String TAG = IncomeOther.class.getSimpleName();
    private MyGridView grImage;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> images;
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private ScrollView scrollView;
    private ExpandableLayout layout;
    private Clothes clothes = new Clothes();
    private ArrayList<Attachment> attach;
    private int appID;

    @Override
    protected int getLayout() {
        return R.layout.fragment_review_deduction_clothes;
    }

    @Override
    protected void initView() {
        findViewById(R.id.fab).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        rbYes = (RadioButtonCustom) findViewById(R.id.rb_yes);
        rbYes.setEnabled(false);
        rbNo = (RadioButtonCustom) findViewById(R.id.rb_no);
        rbNo.setEnabled(false);
        edtType = (EdittextCustom) findViewById(R.id.edt_deduction_type);
        edtType.setEnabled(false);
        edtAmount = (EdittextCustom) findViewById(R.id.edt_amount);
        edtAmount.setEnabled(false);
        grImage = (MyGridView) findViewById(R.id.gr_image);
        grImage.setEnabled(false);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        layout = (ExpandableLayout) findViewById(R.id.layout_expandable);
    }

    @Override
    protected void initData() {
        images = new ArrayList<>();
        attach = new ArrayList<>();
        appID = getApplicationResponse().getId();
        setTitle(getString(R.string.review_income_title));
        appBarVisibility(true, true, 0);
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
        rbYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtils.d(TAG, "setOnCheckedChangeListener : " + b);
                if (b) {
                    layout.setExpanded(true);
                    scollLayout();
                } else {
                    layout.setExpanded(false);
                }
            }
        });
        getReviewDeduction();
    }


    private void updateUI(Clothes b) {
        rbYes.setChecked(b.isHad());
        edtType.setText(b.getType());
        edtAmount.setText(b.getAmount());
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
        File file = new File(FileUtils.getInstance().getHozoDirectory(), "image" + System.currentTimeMillis() + ".jpg");
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

    private void scollLayout() {
        int[] coords = {0, 0};
        scrollView.getLocationOnScreen(coords);
        int absoluteBottom = coords[1] + 250;
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(scrollView, "scrollY", absoluteBottom).setDuration(1000);
        objectAnimator.start();
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
                    clothes = response.body().getClothes();
                    if (clothes != null) updateUI(clothes);
                } else {
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
            govJson.put(Constants.PARAMETER_REVIEW_TYPE, edtType.getText().toString().trim());
            govJson.put(Constants.PARAMETER_REVIEW_AMOUNT, edtAmount.getText().toString().trim());
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
                LogUtils.d(TAG, "doSaveReview code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
//                    LogUtils.d(TAG, "doSaveReview code: " + response.body().getClothes().toString());
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.PARAMETER_APP_ID, appID);
//                    openFragment(R.id.layout_container, FragmentReviewDividends.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
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
                edtType.setEnabled(true);
                edtType.requestFocus();
                edtType.setSelection(edtType.length());
                edtType.setEnabled(true);
                edtAmount.setEnabled(true);
                grImage.setEnabled(true);
//               if (rbYes.isChecked())Utils.showSoftKeyboard(getContext(), edtHow);
                break;
            case R.id.btn_next:
                final Bundle bundle = new Bundle();
                if (rbYes.isChecked()) {
                    if (edtType.getText().toString().trim().isEmpty()) {
                        edtType.getParent().requestChildFocus(edtType, edtType);
                        showToolTipView(getContext(), edtType, Gravity.BOTTOM, getString(R.string.vali_all_empty), ContextCompat.getColor(getContext(), R.color.red));
                        return;
                    }
                    if (edtAmount.getText().toString().trim().isEmpty()) {
                        edtAmount.getParent().requestChildFocus(edtAmount, edtAmount);
                        showToolTipView(getContext(), edtAmount, Gravity.TOP, getString(R.string.vali_all_empty), ContextCompat.getColor(getContext(), R.color.red));
                        return;
                    }
                    if (images.size() < 2) {
                        showToolTipView(getContext(), grImage, Gravity.TOP, getString(R.string.vali_all_empty), ContextCompat.getColor(getContext(), R.color.red));
                        return;
                    }
                    uploadImage();

                } else {
                    bundle.putInt(Constants.PARAMETER_APP_ID, appID);
//                    openFragment(R.id.layout_container, FragmentReviewDividends.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
                }
                break;


        }

    }
}