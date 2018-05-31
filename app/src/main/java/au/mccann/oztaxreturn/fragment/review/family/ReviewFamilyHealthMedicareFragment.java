package au.mccann.oztaxreturn.fragment.review.family;

import android.Manifest;
import android.app.Activity;
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
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.ReviewFamilyHealthResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.FileUtils;
import au.mccann.oztaxreturn.utils.ImageUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.CheckBoxCustom;
import au.mccann.oztaxreturn.view.MyGridView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 5/3/18.
 */

public class ReviewFamilyHealthMedicareFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = ReviewFamilyHealthMedicareFragment.class.getSimpleName();
    private CheckBoxCustom cbYes, cbNo;
    private ReviewFamilyHealthResponse reviewFamilyHealthResponse;

    private MyGridView grImage;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> images;
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private ArrayList<Attachment> attach;

    @Override
    protected int getLayout() {
        return R.layout.review_family_health_medicare_fragment;
    }

    @Override
    protected void initView() {
        grImage = (MyGridView) findViewById(R.id.gr_image);
        cbYes = (CheckBoxCustom) findViewById(R.id.cb_yes);
        cbNo = (CheckBoxCustom) findViewById(R.id.cb_no);
        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        doEdit();
        getReviewProgress(getApplicationResponse());
        setTitle(getString(R.string.review_fhd_title));
        appBarVisibility(true, true, 1);
        getReviewFamilyAndHealth();
        images = new ArrayList<>();
        attach = new ArrayList<>();

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

        cbYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtils.d(TAG, "setOnCheckedChangeListener : " + b);
                if (b) {
                    cbNo.setChecked(false);
                    grImage.setVisibility(View.VISIBLE);
                } else {
                    grImage.setVisibility(View.GONE);
                }
            }
        });

        cbNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtils.d(TAG, "setOnCheckedChangeListener : " + b);
                if (b) {
                    cbYes.setChecked(false);
                    grImage.setVisibility(View.GONE);
                } else {
                    grImage.setVisibility(View.VISIBLE);
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

    private void doUpdate() {
        JSONObject jsonRequest = new JSONObject();
        JSONObject jsonBody = new JSONObject();
        try {
            if (cbYes.isChecked()) {
                for (Image image : images) {
                    if (image.getId() > 0) {
                        Attachment attachment = new Attachment();
                        attachment.setId((int) image.getId());
                        attachment.setUrl(image.getPath());
                        if (attach.contains(attachment))
                            attach.add(attachment);
                    }
                }
                JSONArray jsonArray = new JSONArray();
                for (Attachment mId : attach)
                    jsonArray.put(mId.getId());

                jsonBody.put("attachments", jsonArray);
                jsonBody.put("had", true);
            } else {
                jsonBody.put("had", false);
            }

            jsonRequest.put("medicares", jsonBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtils.d(TAG, "doUpdate jsonRequest : " + jsonRequest.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().updateReviewFamilyHealth(UserManager.getUserToken(), getApplicationResponse().getId(), body).enqueue(new Callback<ReviewFamilyHealthResponse>() {
            @Override
            public void onResponse(Call<ReviewFamilyHealthResponse> call, Response<ReviewFamilyHealthResponse> response) {
                LogUtils.d(TAG, "doUpdate code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doUpdate body : " + response.body().toString());
                    openFragment(R.id.layout_container, ReviewFamilyHealthPrivateFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "doUpdate error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }
                ProgressDialogUtils.dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ReviewFamilyHealthResponse> call, Throwable t) {
                LogUtils.e(TAG, "doUpdate onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doUpdate();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private void doEdit() {
        cbYes.setEnabled(isEditApp());
        cbNo.setEnabled(isEditApp());
    }

    private void doNext() {

        if (cbYes.isChecked()) {
            final ArrayList<Image> listUp = new ArrayList<>();
            for (Image image : images) {
                if (image.getId() == 0 && !image.isAdd()) listUp.add(image);
            }

            if (images.size() == 1) {
                Utils.showLongToast(getActivity(), getString(R.string.image_attach_empty), true, false);
                return;
            }

            LogUtils.d(TAG, "doNext : " + listUp.toString());

            if (listUp.size() > 0) {
                ImageUtils.doUploadImage(getContext(), listUp, new ImageUtils.UpImagesListener() {
                    @Override
                    public void onSuccess(List<Attachment> responses) {
                        attach.addAll(responses);
                        doUpdate();
                    }
                });
            } else {
                doUpdate();
            }

        } else {
            doUpdate();
        }


    }

    private void checkPermissionImageAttach() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
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

    private void updateUI() {
        if (reviewFamilyHealthResponse.getMedicareResponse() != null && reviewFamilyHealthResponse.getMedicareResponse().isHad()) {
            cbYes.setChecked(true);
            cbNo.setChecked(false);

            for (Attachment atachment : reviewFamilyHealthResponse.getMedicareResponse().getAttachments()) {
                Image image = new Image();
                image.setId(atachment.getId());
                image.setAdd(false);
                image.setPath(atachment.getUrl());
                images.add(0, image);
                imageAdapter.notifyDataSetChanged();
            }
            grImage.setVisibility(View.VISIBLE);
        } else {
            cbNo.setChecked(true);
            cbYes.setChecked(false);
            grImage.setVisibility(View.GONE);
        }
    }

    private void getReviewFamilyAndHealth() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        LogUtils.d(TAG, "getReviewFamilyAndHealth getApplicationResponse().getId() : " + getApplicationResponse().getId());

        ApiClient.getApiService().getReviewFamilyHealth(UserManager.getUserToken(), getApplicationResponse().getId()).enqueue(new Callback<ReviewFamilyHealthResponse>() {
            @Override
            public void onResponse(Call<ReviewFamilyHealthResponse> call, Response<ReviewFamilyHealthResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getReviewFamilyAndHealth code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getReviewFamilyAndHealth body : " + response.body().toString());
                    reviewFamilyHealthResponse = response.body();
                    updateUI();
                } else if (response.code() == Constants.HTTP_CODE_BLOCK) {
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getReviewFamilyAndHealth error : " + error.message());
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<ReviewFamilyHealthResponse> call, Throwable t) {
                LogUtils.e(TAG, "getReviewFamilyAndHealth onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getReviewFamilyAndHealth();
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
                if (isEditApp()) doNext();
                else
                    openFragment(R.id.layout_container, ReviewFamilyHealthPrivateFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;
        }
    }
}
