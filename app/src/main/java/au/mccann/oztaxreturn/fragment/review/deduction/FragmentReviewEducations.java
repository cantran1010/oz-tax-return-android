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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.AlbumActivity;
import au.mccann.oztaxreturn.activity.PreviewImageActivity;
import au.mccann.oztaxreturn.adapter.EducationAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.dialog.PickImageDialog;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.DeductionResponse;
import au.mccann.oztaxreturn.model.Education;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.FileUtils;
import au.mccann.oztaxreturn.utils.ImageUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.ButtonCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CanTran on 4/23/18.
 */
public class FragmentReviewEducations extends BaseFragment implements View.OnClickListener {
    private static final String TAG = FragmentReviewEducations.class.getSimpleName();
    private EducationAdapter adapter;
    private ArrayList<Education> educations = new ArrayList<>();
    private RecyclerView recyclerView;
    private int appID;
    private ArrayList<Image> images = new ArrayList<>();
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private int countDown = 0;
    private FloatingActionButton fab;

    @Override
    protected int getLayout() {
        return R.layout.fragment_review_deduction_educations;
    }

    @Override
    protected void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        ButtonCustom btnnext = (ButtonCustom) findViewById(R.id.btn_next);
        btnnext.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.rcv_education);

    }


    @Override
    protected void initData() {
        updateProgress(14);
        appID = getApplicationResponse().getId();
        fab.setEnabled(isEditApp());
        setTitle(getString(R.string.review_income_title));
        appBarVisibility(true, true, 1);
        updateList();
        getReviewDeduction();
    }

    private void updateUI(ArrayList<Education> ds) {
        educations.clear();
        educations.addAll(ds);
        for (Education education : educations
                ) {
            AddIconAdd(education);
            showImage(education.getAttachments(), education.getImages());
        }
        adapter.notifyDataSetChanged();

    }

    public static void showImage(ArrayList<Attachment> attachments, ArrayList<Image> images) {
        if (attachments.size() > 0) {
            for (Attachment attachment : attachments
                    ) {
                Image image = new Image();
                image.setId(attachment.getId());
                image.setAdd(false);
                image.setPath(attachment.getUrl());
                images.add(0, image);
            }

        }
    }

    private void updateList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new EducationAdapter(getContext(), educations);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickImageListener(new EducationAdapter.OnClickImageListener() {
            @Override
            public void onClick(int position, int n) {
                LogUtils.d(TAG, "setOnClickImageListener" + n + " position " + position + educations.get(position).getImages().toString());
                images = educations.get(position).getImages();
                if (images.get(n).isAdd) {
                    LogUtils.d(TAG, "setOnClickImageListener 3");
                    if (images.size() >= 10) {
                        Utils.showLongToast(getActivity(), getString(R.string.max_image_attach_err, 9), true, false);
                    } else {
                        checkPermissionImageAttach();
                    }
                } else {
                    LogUtils.d(TAG, "setOnClickImageListener 1");
                    Intent intent = new Intent(getActivity(), PreviewImageActivity.class);
                    intent.putExtra(Constants.EXTRA_IMAGE_PATH, images.get(position).getPath());
                    startActivity(intent, TransitionScreen.RIGHT_TO_LEFT);
                }
            }
        });

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
            adapter.notifyDataSetChanged();
        } else if (requestCode == Constants.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            final String selectedImagePath = getImagePath();
            LogUtils.d(TAG, "onActivityResult selectedImagePath : " + selectedImagePath);
            Image image = new Image();
            image.setId(0);
            image.setAdd(false);
            image.setPath(selectedImagePath);
            images.add(0, image);
            adapter.notifyDataSetChanged();
        }
    }

    private String getImagePath() {
        return imgPath;
    }

    private void getReviewDeduction() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        LogUtils.d(TAG, "getReviewDeduction id : " + appID);
        ApiClient.getApiService().getReviewDeduction(UserManager.getUserToken(), appID).enqueue(new Callback<DeductionResponse>() {
            @Override
            public void onResponse(Call<DeductionResponse> call, Response<DeductionResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getReviewDeduction code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getReviewDeduction body : " + response.body().getEducations().toString());
                    updateUI(response.body().getEducations());
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

    private void uploadImage(final ArrayList<Education> ds) {

        for (final Education e1 : ds
                ) {
            e1.setListUp(new ArrayList<Image>());
            for (Image image : e1.getImages()
                    ) {
                if (image.getId() == 0 && !image.isAdd()) e1.getListUp().add(image);
            }
        }

        for (Education e2 : ds
                ) {
            if (e2.getListUp().size() > 0) countDown++;
        }
        if (countDown == 0) doSaveReview();
        else {
            for (final Education e3 : ds
                    ) {
                if (e3.getListUp().size() > 0) {
                    ImageUtils.doUploadImage(getContext(), e3.getListUp(), new ImageUtils.UpImagesListener() {
                        @Override
                        public void onSuccess(List<Attachment> responses) {
                            countDown--;
                            e3.getAttach().addAll(responses);
                            if (countDown == 0) doSaveReview();
                        }
                    });
                }

            }

        }
    }


    private void doSaveReview() {
        ProgressDialogUtils.showProgressDialog(getContext());
        LogUtils.d(TAG, "doSaveReview" + educations.toString());
        JSONObject jsonRequest = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (Education d : educations
                    ) {
                JSONObject mJs = new JSONObject();
                mJs.put(Constants.PARAMETER_PUT_ID, d.getId());
                mJs.put(Constants.PARAMETER_REVIEW_TYPE, d.getType());
                mJs.put(Constants.PARAMETER_REVIEW_DEDUCTION_COURSE, d.getCourse());
                mJs.put(Constants.PARAMETER_REVIEW_AMOUNT, d.getAmount());
                if (d.getImages().size() > 1) {
                    for (Image image : d.getImages()
                            ) {
                        if (image.getId() > 0) {
                            Attachment attachment = new Attachment();
                            attachment.setId((int) image.getId());
                            attachment.setUrl(image.getPath());
                            d.getAttach().add(attachment);
                        }
                    }
                    JSONArray js = new JSONArray();
                    for (Attachment mId : d.getAttach())
                        js.put(mId.getId());
                    mJs.put(Constants.PARAMETER_ATTACHMENTS, js);
                } else {
                    mJs.put(Constants.PARAMETER_ATTACHMENTS, new JSONArray());
                }
                jsonArray.put(mJs);
            }
            if (adapter.isExpend())
                jsonRequest.put(Constants.PARAMETER_REVIEW_INCOME_EDUCATIONS, jsonArray);
            else jsonRequest.put(Constants.PARAMETER_REVIEW_INCOME_EDUCATIONS, new JSONArray());
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
                    LogUtils.d(TAG, "doSaveReview body: " + response.body().getEducations().toString());
                    LogUtils.d(TAG, " dividends image " + educations.toString());
                    openFragment(R.id.layout_container, FragmentReviewOthers.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
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


    public void AddIconAdd(Education education) {
        if (education.getImages() == null || education.getImages().size() == 0) {
            final Image image = new Image();
            image.setId(0);
            image.setAdd(true);
            education.getImages().add(image);
        }
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                adapter.setEdit(true);
                adapter.notifyDataSetChanged();
                break;
            case R.id.btn_next:
                if (isEditApp()) {
                    if (adapter.isExpend())
                        uploadImage(educations);
                    else {
                        doSaveReview();
                    }
                } else
                    openFragment(R.id.layout_container, FragmentReviewOthers.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;
        }

    }
}
