package au.mccann.oztaxreturn.fragment;

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
public class GovementPayment extends BaseFragment implements View.OnClickListener {
    private RadioButtonCustom rbYes, rbNo;
    private EdittextCustom edtIncomeType, edtGrossPayment, edtTax;
    private MyGridView myGridView;
    private static final String TAG = IncomeOther.class.getSimpleName();
    private MyGridView grImage;
    private ImageAdapter imageAdapter;
    private final ArrayList<Image> images = new ArrayList<>();
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private Bundle bundle = new Bundle();
    private ScrollView scrollView;
    private ExpandableLayout layout;
    private IncomeResponse incomeResponse = new IncomeResponse();
    private GovPayment govPayment = new GovPayment();
    private ArrayList<Integer> attachID = new ArrayList<>();


    @Override
    protected int getLayout() {
        return R.layout.fragment_review_govement_payment;
    }

    @Override
    protected void initView() {
        findViewById(R.id.fab).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        rbYes = (RadioButtonCustom) findViewById(R.id.rb_yes);
        rbYes.setEnabled(false);
        rbNo = (RadioButtonCustom) findViewById(R.id.rb_no);
        rbNo.setEnabled(false);
        edtIncomeType = (EdittextCustom) findViewById(R.id.edt_income_type);
        edtIncomeType.setEnabled(false);
        edtGrossPayment = (EdittextCustom) findViewById(R.id.edt_gross_payment);
        edtGrossPayment.setEnabled(false);
        edtTax = (EdittextCustom) findViewById(R.id.edt_tax_widthheld);
        edtTax.setEnabled(false);
        myGridView = (MyGridView) findViewById(R.id.gr_image);
        myGridView.setEnabled(false);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        layout = (ExpandableLayout) findViewById(R.id.layout_expandable);
        grImage = (MyGridView) findViewById(R.id.gr_image);

    }

    @Override
    protected void initData() {
        bundle = getArguments();
        incomeResponse = (IncomeResponse) bundle.getSerializable(Constants.KEY_REVIEW);
        LogUtils.d(TAG, "initData :" + incomeResponse.toString());
        govPayment = incomeResponse.getGovPayment();
        setTitle(getString(R.string.review_income_title));
        appBarVisibility(true, true);
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
        rbYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtils.d(TAG, "setOnCheckedChangeListener : " + b);
                if (b) {
                    layout.setExpanded(true);
                    scollLayout();
                    edtIncomeType.requestFocus();
                    edtIncomeType.setSelection(edtIncomeType.length());
                } else {
                    layout.setExpanded(false);
                }
            }
        });
        if (govPayment != null) setData();
    }

    private void setData() {
        rbYes.setChecked(govPayment.isHad());
        edtIncomeType.setText(govPayment.getType());
        edtGrossPayment.setText(govPayment.getGross());
        edtTax.setText(govPayment.getTax());
        showImage();
    }

    private void showImage() {
        if (govPayment.getAttachments().size() > 0) {
            for (Attachment attachment : govPayment.getAttachments()
                    ) {
                Image image = new Image();
                image.setId(attachment.getId());
                image.setAdd(false);
                image.setPath(attachment.getUrl());
                images.add(0, image);
            }
            imageAdapter.notifyDataSetChanged();
//            for (int i = 0; i < countImageCopy; i++) {
//                final int finalI = i;
//                Glide.with(this)
//                        .load(govPayment.getAttachments().get(i))
//                        .asBitmap()
//                        .into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                LogUtils.d(TAG, "onResourceReady complete , resource , width : " + resource.getWidth() + " , height : " + resource.getHeight());
//                                resource = resource.copy(resource.getConfig(), true); // safe copy
//                                Glide.clear(this);
//                                @SuppressWarnings("AccessStaticViaInstance") File fileSave = new File(FileUtils.getInstance().getHozoDirectory(), "image" + System.currentTimeMillis() + ".jpg");
//                                Utils.compressBitmapToFile(resource, fileSave.getPath());
//                                LogUtils.d(TAG, "onResourceReady complete , path : " + fileSave.getPath());
//                                Image imageCopy = new Image();
//                                imageCopy.setId(govPayment.getAttachments().get(finalI).getId());
//                                imageCopy.setAdd(false);
//                                imageCopy.setPath(fileSave.getPath());
//                                images.add(imageCopy);
//
//                                countImageCopy--;
//                                if (countImageCopy == 0) {
//                                    ProgressDialogUtils.dismissProgressDialog();
//                                }
//
//                            }
//                        });
//            }
        }


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
                showImage();
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
        int absoluteBottom = coords[1] + scrollView.getHeight();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(scrollView, "scrollY", absoluteBottom).setDuration(1500);
        objectAnimator.start();
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    private void doPutReview() {
        ProgressDialogUtils.showProgressDialog(getContext());
        final JSONObject jsonRequest = new JSONObject();
        try {
            JSONObject govJson = new JSONObject();
            govJson.put("had", rbYes.isChecked());
            govJson.put("income_type", edtIncomeType.getText().toString().trim());
            govJson.put("tax_withheld", edtTax.getText().toString().trim());
            govJson.put("gross_income", edtGrossPayment.getText().toString().trim());
            if (images.size() > 1) {
                for (Image image : images
                        ) {
                    if (image.getId() > 0) attachID.add((int) image.getId());
                }
                JSONArray jsonArray = new JSONArray();
                for (int mId : attachID)
                    jsonArray.put(mId);
                govJson.put("attachments", jsonArray);
            }

            jsonRequest.put("gov_payments", govJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtils.d(TAG, "doPutReview jsonRequest: " + jsonRequest);
        LogUtils.d(TAG, "doPutReview appID: " + bundle.getInt(Constants.PARAMETER_APP_ID));
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().putReviewIncom(UserManager.getUserToken(), bundle.getInt(Constants.PARAMETER_APP_ID), body).enqueue(new Callback<IncomeResponse>() {
            @Override
            public void onResponse(Call<IncomeResponse> call, Response<IncomeResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "doPutReview code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doPutReview body: " + response.code());
                    incomeResponse = response.body();
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.e(TAG, "doPutReview error : " + error.message());
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
                LogUtils.e(TAG, "doPutReview onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getContext(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doPutReview();
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
            if (image.getId() == 0) listUp.add(image);
        }
        if (listUp.size() > 0) {
            ImageUtils.doUploadImage(getContext(), listUp, new ImageUtils.UpImagesListener() {
                @Override
                public void onSuccess(List<Attachment> responses) {
                    for (Attachment attachment : responses
                            ) {
                        attachID.add(attachment.getId());
                    }
                    doPutReview();
                }
            });
        } else {
            doPutReview();
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                rbYes.setEnabled(true);
                rbNo.setEnabled(true);
                edtIncomeType.setEnabled(true);
                edtGrossPayment.setEnabled(true);
                edtTax.setEnabled(true);
                myGridView.setEnabled(true);
                break;
            case R.id.btn_next:
                if (rbYes.isChecked()) {
                    govPayment = new GovPayment();
                    govPayment.setHad(true);
                    govPayment.setType(edtIncomeType.getText().toString().trim());
                    govPayment.setGross(edtGrossPayment.getText().toString().trim());
                    govPayment.setTax(edtTax.getText().toString().trim());
                    LogUtils.d(TAG, "next " + images.size());
                    if (images.size() > 1) {
                        uploadImage();
                    } else
                        doPutReview();
//                openFragment(R.id.layout_container, GovementPayment.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
                }
                break;

        }

    }
}
