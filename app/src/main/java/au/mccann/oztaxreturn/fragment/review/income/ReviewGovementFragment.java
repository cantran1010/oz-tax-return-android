package au.mccann.oztaxreturn.fragment.review.income;

import android.Manifest;
import android.app.Activity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
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
import au.mccann.oztaxreturn.dialog.PickImageDialog;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.fragment.basic.OtherFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.GovPayment;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.model.IncomeResponse;
import au.mccann.oztaxreturn.networking.ApiClient;
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


/**
 * Created by CanTran on 4/24/18.
 */
public class ReviewGovementFragment extends BaseFragment implements View.OnClickListener {
    private RadioButtonCustom rbYes, rbNo;
    private EdittextCustom edtIncomeType;
    private EditTextEasyMoney edtGrossPayment, edtTax;
    private static final String TAG = OtherFragment.class.getSimpleName();
    private MyGridView grImage;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> images;
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private ExpandableLayout layout;
    private GovPayment govPayment = new GovPayment();
    private ArrayList<Attachment> attach;
    private int appID;

    @Override
    protected int getLayout() {
        return R.layout.fragment_review_govement_payment;
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_next).setOnClickListener(this);
        rbYes = (RadioButtonCustom) findViewById(R.id.rb_yes);
        rbNo = (RadioButtonCustom) findViewById(R.id.rb_no);
        edtIncomeType = (EdittextCustom) findViewById(R.id.edt_income_type);
        edtGrossPayment = (EditTextEasyMoney) findViewById(R.id.edt_gross_payment);
        edtTax = (EditTextEasyMoney) findViewById(R.id.edt_tax_widthheld);
        grImage = (MyGridView) findViewById(R.id.gr_image);
        layout = (ExpandableLayout) findViewById(R.id.layout_expandable);

    }

    @Override
    protected void initData() {
        ((HomeActivity) getActivity()).setIndex(13);
        rbYes.setEnabled(isEditApp());
        rbNo.setEnabled(isEditApp());
        edtGrossPayment.setEnabled(isEditApp());
        edtIncomeType.setEnabled(isEditApp());
        edtTax.setEnabled(isEditApp());
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
        imageAdapter.notifyDataSetChanged();
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
                    edtIncomeType.requestFocus();
                    edtIncomeType.setSelection(edtIncomeType.length());
                } else {
                    layout.setExpanded(false);
                }
            }
        });
        getReviewIncome();
    }


    private void updateUI(GovPayment govPayment) {
        rbYes.setChecked(govPayment.isHad());
        if (govPayment.isHad()) {
            edtIncomeType.setText(govPayment.getType());
            edtGrossPayment.setText(govPayment.getGross());
            edtTax.setText(govPayment.getTax());
        }
        ImageUtils.showImage(govPayment.getAttachments(), images, imageAdapter);
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
//                    LogUtils.d(TAG, "getReviewIncome body : " + response.body().getGovPayment().toString());
                    govPayment = response.body().getGovPayment();
                    if (govPayment != null) updateUI(govPayment);
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
                govJson.put(Constants.PARAMETER_REVIEW_INCOME_GOVEMENT_TYPE, edtIncomeType.getText().toString().trim());
                govJson.put(Constants.PARAMETER_REVIEW_INCOME_GOVEMENT_GROSS, edtGrossPayment.getValuesFloat());
                govJson.put(Constants.PARAMETER_REVIEW_INCOME_GOVEMENT_TAX, edtTax.getValuesFloat());

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
            jsonRequest.put(Constants.PARAMETER_REVIEW_INCOME_GOVEMENT, govJson);

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
                    openFragment(R.id.layout_container, ReviewInterestsFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (isEditApp()) {
                    if (rbYes.isChecked()) {
                        if (edtIncomeType.getText().toString().trim().isEmpty()) {
                            edtIncomeType.requestFocus();
                            edtIncomeType.getParent().requestChildFocus(edtIncomeType, edtIncomeType);
                            Utils.showToolTip(getContext(), edtIncomeType, getString(R.string.vali_all_empty));
                            return;
                        }
                        if (edtGrossPayment.getText().toString().trim().isEmpty()) {
                            edtGrossPayment.requestFocus();
                            edtGrossPayment.getParent().requestChildFocus(edtGrossPayment, edtGrossPayment);
                            Utils.showToolTip(getContext(), edtGrossPayment, getString(R.string.vali_all_empty));
                            return;
                        }
                        if (edtTax.getText().toString().trim().isEmpty()) {
                            edtTax.requestFocus();
                            edtTax.getParent().requestChildFocus(edtTax, edtTax);
                            Utils.showToolTip(getContext(), edtTax, getString(R.string.vali_all_empty));
                            return;
                        }
//                        if (images.size() < 2) {
//                            grImage.getParent().requestChildFocus(grImage, grImage);
//                            Utils.showToolTip(getContext(), grImage, getString(R.string.err_pick_image));
//                            return;
//                        }
                        uploadImage();

                    } else {
                        doSaveReview();
                    }
                } else
                    openFragment(R.id.layout_container, ReviewInterestsFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;


        }

    }
}
