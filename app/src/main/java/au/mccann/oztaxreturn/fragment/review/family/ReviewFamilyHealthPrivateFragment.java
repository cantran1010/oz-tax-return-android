package au.mccann.oztaxreturn.fragment.review.family;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.AlbumActivity;
import au.mccann.oztaxreturn.adapter.ReviewFHPAdapter;
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
import au.mccann.oztaxreturn.rest.response.ReviewPrivateResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.FileUtils;
import au.mccann.oztaxreturn.utils.ImageUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.CheckBoxCustom;
import au.mccann.oztaxreturn.view.EdittextCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 5/3/18.
 */

public class ReviewFamilyHealthPrivateFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = ReviewFamilyHealthPrivateFragment.class.getSimpleName();
    private FloatingActionButton fab, fbAdd;
    private CheckBoxCustom cbYes, cbNo;
    private LinearLayout layoutYes;
    private EdittextCustom edtNumber;
    private ReviewFamilyHealthResponse reviewFamilyHealthResponse;
    private ArrayList<ReviewPrivateResponse> reviewPrivateResponses = new ArrayList<>();
    private RecyclerView rcvList;
    private ReviewFHPAdapter reviewFHPAdapter;
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private int positionPickImage = 0;
    private int countDown;
    private RelativeLayout layoutAdd;

    @Override
    protected int getLayout() {
        return R.layout.review_family_health_private_fragment;
    }

    @Override
    protected void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        cbYes = (CheckBoxCustom) findViewById(R.id.cb_yes);
        cbNo = (CheckBoxCustom) findViewById(R.id.cb_no);

        layoutYes = (LinearLayout) findViewById(R.id.layout_yes);

        fbAdd = (FloatingActionButton) findViewById(R.id.add);

        findViewById(R.id.btn_next).setOnClickListener(this);
        fbAdd.setOnClickListener(this);
        rcvList = (RecyclerView) findViewById(R.id.rcv);

        layoutAdd = (RelativeLayout) findViewById(R.id.layout_add);
    }

    @Override
    protected void initData() {
        getReviewProgress(getApplicationResponse());
        setTitle(getString(R.string.review_fhd_title));
        appBarVisibility(true, true, 1);
        if (isEditApp()) fab.setVisibility(View.VISIBLE);
        else fab.setVisibility(View.GONE);
        fbAdd.setEnabled(false);
        cbYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtils.d(TAG, "setOnCheckedChangeListener : " + b);
                if (b) {
                    cbNo.setChecked(false);
                    rcvList.setVisibility(View.VISIBLE);
                    layoutAdd.setVisibility(View.VISIBLE);
                } else {
                    rcvList.setVisibility(View.GONE);
                    layoutAdd.setVisibility(View.GONE);
                }
            }
        });

        cbNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtils.d(TAG, "setOnCheckedChangeListener : " + b);
                if (b) {
                    cbYes.setChecked(false);
                    rcvList.setVisibility(View.GONE);
                    layoutAdd.setVisibility(View.GONE);
                } else {
                    rcvList.setVisibility(View.VISIBLE);
                    layoutAdd.setVisibility(View.VISIBLE);
                }
            }
        });

        setUpList();
        getReviewFamilyAndHealth();
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    private void setUpList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setAutoMeasureEnabled(true);
        rcvList.setLayoutManager(layoutManager);
        rcvList.setNestedScrollingEnabled(false);
        rcvList.setHasFixedSize(true);
        reviewFHPAdapter = new ReviewFHPAdapter(reviewPrivateResponses, getActivity());
        rcvList.setAdapter(reviewFHPAdapter);

        reviewFHPAdapter.setPickImageListener(new ReviewFHPAdapter.PickImageListener() {
            @Override
            public void doPick(int position) {

                if (isEditApp()) {
                    LogUtils.d(TAG, "doPick , position : " + position);
                    positionPickImage = position;
                    checkPermissionImageAttach(position);
                }

            }
        });
        reviewFHPAdapter.setOnRemoveItem(new ReviewFHPAdapter.OnRemoveItem() {
            @Override
            public void onDelete(final int position) {
                DialogUtils.showOkAndCancelDialog(getActivity(), getString(R.string.app_name), getString(R.string.remove), getString(R.string.Yes), getString(R.string.No), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        reviewPrivateResponses.remove(position);
                        reviewFHPAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancel() {

                    }
                });

            }
        });

    }

    private void checkPermissionImageAttach(int position) {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) + ContextCompat
                .checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, Constants.PERMISSION_REQUEST_CODE);
        } else {
            permissionGrantedImageAttach(position);
        }
    }

    private void permissionGrantedImageAttach(final int position) {
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
                intent.putExtra(Constants.COUNT_IMAGE_ATTACH_EXTRA, reviewPrivateResponses.get(position).getAttachments() == null ? 0 :
                        reviewPrivateResponses.get(position).getAttachments().size());
                startActivityForResult(intent, Constants.REQUEST_CODE_PICK_IMAGE, TransitionScreen.RIGHT_TO_LEFT);
            }
        });
        pickImageDialog.showView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // image attach
        if (requestCode == Constants.REQUEST_CODE_PICK_IMAGE
                && resultCode == Constants.RESPONSE_CODE_PICK_IMAGE
                && data != null) {
            ArrayList<Image> imagesSelected = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);

            for (Image image : imagesSelected) {
                Attachment attachment = new Attachment();
                attachment.setId(0);
                attachment.setUrl(image.getPath());
                reviewPrivateResponses.get(positionPickImage).getAttachments().add(0, attachment);
            }
            reviewFHPAdapter.notifyDataSetChanged();

            LogUtils.d(TAG, "onActivityResult , reviewPrivateResponses : " + reviewPrivateResponses.toString());
        } else if (requestCode == Constants.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            final String selectedImagePath = getImagePath();
//            LogUtils.d(TAG, "onActivityResult selectedImagePath : " + selectedImagePath);
//            Image image = new Image();
//            image.setId(0);
//            image.setAdd(false);
//            image.setPath(selectedImagePath);
//            images.add(0, image);
//            imageAdapter.notifyDataSetChanged();


            Attachment attachment = new Attachment();
            attachment.setId(0);
            attachment.setUrl(selectedImagePath);
            reviewPrivateResponses.get(positionPickImage).getAttachments().add(0, attachment);
            reviewFHPAdapter.notifyDataSetChanged();
        }
    }

    private String getImagePath() {
        return imgPath;
    }

    private Uri setImageUri() {
        File file = new File(FileUtils.getInstance().getMaximyzDirectory(), "image" + System.currentTimeMillis() + ".jpg");
        Uri imgUri = Uri.fromFile(file);
        this.imgPath = file.getAbsolutePath();
        return imgUri;
    }


    private void doUploadImage() {
        countDown = reviewPrivateResponses.size();
        if (reviewPrivateResponses.size() == 0) {
            doNext();
        } else {
            for (int i = 0; i < reviewPrivateResponses.size(); i++) {
                ArrayList<Attachment> attachments = (ArrayList<Attachment>) reviewPrivateResponses.get(i).getAttachments();
                ArrayList<Image> imagesUp = new ArrayList<>();
                LogUtils.d(TAG, "doUploadImage , attachments size : " + attachments.size());
                LogUtils.d(TAG, "doUploadImage , attachments : " + attachments.toString());

                for (Attachment attachment : attachments) {
                    if (attachment.getId() == 0) {
                        Image image = new Image();
                        image.setId(0);
                        image.setPath(attachment.getUrl());
                        image.setName(new File(attachment.getUrl()).getName());
                        imagesUp.add(image);
                    }
                }
                LogUtils.d(TAG, "doUploadImage , imagesUp size : " + imagesUp.size());

                if (imagesUp.size() > 0) {
                    final int finalI = i;
                    ImageUtils.doUploadImage(getActivity(), imagesUp, new ImageUtils.UpImagesListener() {
                        @Override
                        public void onSuccess(List<Attachment> responses) {
                            countDown--;
                            ((ArrayList<Attachment>) reviewPrivateResponses.get(finalI).getAttachments()).addAll(responses);
                            LogUtils.d(TAG, "doUploadImage , count : " + countDown);
                            if (countDown == 0) doNext();
                        }
                    });
                } else {
                    doNext();
                    return;
                }
            }
        }

    }

    private void doNext() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        JSONObject jsonRequest = new JSONObject();

        try {
            if (cbYes.isChecked()) {

                JSONArray jsonArray = new JSONArray();
                for (ReviewPrivateResponse reviewPrivateResponse : reviewPrivateResponses) {

                    if (TextUtils.isEmpty(reviewPrivateResponse.getInsurer())
                            || TextUtils.isEmpty(reviewPrivateResponse.getMembershipNo())
                            || Float.parseFloat(reviewPrivateResponse.getPremiumsPaid()) == 0
                            || Float.parseFloat(reviewPrivateResponse.getGovRebateReceived()) == 0
                            || reviewPrivateResponse.getDaysCovered() == 0
                            ) {

                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), getString(R.string.required_all), getString(R.string.Yes), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                        ProgressDialogUtils.dismissProgressDialog();
                        return;
                    }

                    JSONObject oneObject = new JSONObject();
                    oneObject.put("id", reviewPrivateResponse.getId());
                    oneObject.put("insurer", reviewPrivateResponse.getInsurer());
                    oneObject.put("membership_no", reviewPrivateResponse.getMembershipNo());
                    oneObject.put("premiums_paid", reviewPrivateResponse.getPremiumsPaid());
                    oneObject.put("gov_rebate_received", reviewPrivateResponse.getGovRebateReceived());
                    oneObject.put("days_covered", reviewPrivateResponse.getDaysCovered());

                    JSONArray jsonArrayAttach = new JSONArray();
                    for (Attachment mId : reviewPrivateResponse.getAttachments()) {
                        if (mId.getId() != 0) jsonArrayAttach.put(mId.getId());
                    }

                    oneObject.put("attachments", jsonArrayAttach);
                    jsonArray.put(oneObject);

                }

                jsonRequest.put("privates", jsonArray);

            } else {
                jsonRequest.put("privates", new JSONArray());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtils.d(TAG, "doNext jsonRequest : " + jsonRequest.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().updateReviewFamilyHealth(UserManager.getUserToken(), getApplicationResponse().getId(), body).enqueue(new Callback<ReviewFamilyHealthResponse>() {
            @Override
            public void onResponse(Call<ReviewFamilyHealthResponse> call, Response<ReviewFamilyHealthResponse> response) {
                LogUtils.d(TAG, "doNext code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doNext body : " + response.body().toString());
                    openFragment(R.id.layout_container, ReviewFamilyHealthSpouseFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "doNext error : " + error.message());
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
                LogUtils.e(TAG, "doNext onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doNext();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private void doEdit() {
        cbYes.setEnabled(true);
        cbNo.setEnabled(true);
        reviewFHPAdapter.setEdit(true);
        reviewFHPAdapter.notifyDataSetChanged();
        fbAdd.setEnabled(true);
    }

    private void updateUI() {
        if (reviewPrivateResponses != null && reviewPrivateResponses.size() == 0) {
            cbYes.setChecked(false);
            cbNo.setChecked(true);
        } else {
            cbYes.setChecked(true);
            cbNo.setChecked(false);
            setUpList();
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
                    reviewPrivateResponses = (ArrayList<ReviewPrivateResponse>) reviewFamilyHealthResponse.getPrivates();
                    updateUI();
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

    private void doAdd() {

        if (reviewPrivateResponses.size() >= 3) {
            Utils.showLongToast(getActivity(), getString(R.string.max_3_add), true, false);
        } else {
            ReviewPrivateResponse reviewPrivateResponse = new ReviewPrivateResponse();
            reviewPrivateResponses.add(reviewPrivateResponse);
            reviewFHPAdapter.setEdit(true);
            reviewFHPAdapter.notifyDataSetChanged();
            LogUtils.d(TAG, "doAdd , reviewPrivateResponses size : " + reviewPrivateResponses.size());
            setUpList();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_next:
                if (isEditApp()) doUploadImage();
                else
                    openFragment(R.id.layout_container, ReviewFamilyHealthSpouseFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;

            case R.id.fab:
                doEdit();
                break;

            case R.id.add:
                doAdd();
                break;

        }
    }

}
