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
import au.mccann.oztaxreturn.activity.HomeActivity;
import au.mccann.oztaxreturn.activity.PreviewImageActivity;
import au.mccann.oztaxreturn.activity.SplashActivity;
import au.mccann.oztaxreturn.adapter.AnnuitiesAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.dialog.PickImageDialog;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Annuity;
import au.mccann.oztaxreturn.model.Attachment;
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
import au.mccann.oztaxreturn.view.ButtonCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CanTran on 4/23/18.
 */
public class ReviewAnnuitiesFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = ReviewDividendsFragment.class.getSimpleName();
    private AnnuitiesAdapter adapter;
    private ArrayList<Annuity> annuities = new ArrayList<>();
    private RecyclerView recyclerView;
    private int appID;
    private ArrayList<Image> images = new ArrayList<>();
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private int countDown = 0;

    @Override
    protected int getLayout() {
        return R.layout.fragment_review_income_dividend;
    }

    @Override
    protected void initView() {
        ButtonCustom btnnext = (ButtonCustom) findViewById(R.id.btn_next);
        btnnext.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.rcv_job);

    }


    @Override
    protected void initData() {
        ((HomeActivity) getActivity()).setIndex(17);
        getReviewProgress(getApplicationResponse());
        appID = getApplicationResponse().getId();
        setTitle(getString(R.string.review_income_title));
        appBarVisibility(true, true, 1);
        getReviewIncome();
    }

    private void updateUI(ArrayList<Annuity> ds) {
        annuities.clear();
        annuities.addAll(ds);
        for (Annuity dividend : annuities
                ) {
            AddIconAdd(dividend);
            showImage(dividend.getAttachments(), dividend.getImages());
        }
        updateList();

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
        adapter = new AnnuitiesAdapter(getContext(), annuities, isEditApp());
        recyclerView.setAdapter(adapter);
        adapter.setOnClickImageListener(new AnnuitiesAdapter.OnClickImageListener() {
            @Override
            public void onClick(int position, int n) {
                LogUtils.d(TAG, "setOnClickImageListener" + n + " position " + position + annuities.get(position).getImages().toString());
                images = annuities.get(position).getImages();
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
                    intent.putExtra(Constants.EXTRA_IMAGE_PATH, images.get(n).getPath());
                    startActivity(intent, TransitionScreen.RIGHT_TO_LEFT);
                }
            }
        });
        adapter.setOnRemoveItem(new AnnuitiesAdapter.OnRemoveItem() {
            @Override
            public void onDelete(final int position) {
                DialogUtils.showOkAndCancelDialog(getActivity(), getString(R.string.app_name_old), getString(R.string.remove), getString(R.string.Yes), getString(R.string.No), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        annuities.remove(position);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancel() {

                    }
                });

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

    private void getReviewIncome() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        LogUtils.d(TAG, "getReviewIncome id : " + appID);
        ApiClient.getApiService().getReviewIncome(UserManager.getUserToken(), appID).enqueue(new Callback<IncomeResponse>() {
            @Override
            public void onResponse(Call<IncomeResponse> call, Response<IncomeResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getReviewIncome code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getReviewIncome body : " + response.body().getAnnuities().toString());
                    updateUI(response.body().getAnnuities());
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

    private void uploadImage(final ArrayList<Annuity> ds) {
        for (final Annuity dividend : ds
                ) {
            dividend.setListUp(new ArrayList<Image>());
            for (Image image : dividend.getImages()
                    ) {
                if (image.getId() == 0 && !image.isAdd()) dividend.getListUp().add(image);
            }
        }

        for (Annuity dividend1 : ds
                ) {
            if (dividend1.getListUp().size() > 0) countDown++;
        }

        if (countDown == 0) doSaveReview();
        else {
            for (final Annuity d : ds
                    ) {
                if (d.getListUp().size() > 0) {
                    LogUtils.d(TAG, "doUploadImage count" + countDown + annuities.toString());
                    ImageUtils.doUploadImage(getContext(), d.getListUp(), new ImageUtils.UpImagesListener() {
                        @Override
                        public void onSuccess(List<Attachment> responses) {
                            countDown--;
                            d.getAttach().addAll(responses);
                            LogUtils.d(TAG, "doUploadImage finalCount" + countDown + annuities.toString());
                            if (countDown == 0) {
                                FileUtils.deleteDirectory(new File(FileUtils.OUTPUT_DIR));
                                doSaveReview();
                            }
                        }
                    });
                }

            }

        }
    }


    private void doSaveReview() {
        ProgressDialogUtils.showProgressDialog(getContext());
        LogUtils.d(TAG, "doSaveReview" + annuities.toString());
        JSONObject jsonRequest = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (Annuity d : annuities
                    ) {
                JSONObject mJs = new JSONObject();
                mJs.put(Constants.PARAMETER_PUT_ID, d.getId());
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_ANNUITY_TAX, d.getTaxWithheld());
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_ANNUITY_COM_TAX, d.getTaxableComTaxed());
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_ANNUITY_COM_UNTAX, d.getTaxableComUntaxed());
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_ANNUITY_ARREAR_TAX, d.getArrearsTaxed());
                mJs.put(Constants.PARAMETER_REVIEW_INCOM_ANNUITY_ARREAR_UNTAX, d.getArrearsUntaxed());
                if (d.getImages().size() > 1) {
                    for (Image image : d.getImages()
                            ) {
                        if (image.getId() > 0) {
                            Attachment attachment = new Attachment();
                            attachment.setId((int) image.getId());
                            attachment.setUrl(image.getPath());
                            if (!d.getAttach().contains(attachment))
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
                jsonRequest.put(Constants.PARAMETER_REVIEW_INCOME_ANNUITY, jsonArray);
            else jsonRequest.put(Constants.PARAMETER_REVIEW_INCOME_ANNUITY, new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "doSaveReview jsonRequest : " + jsonRequest.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().putReviewIncom(UserManager.getUserToken(), appID, body).enqueue(new Callback<IncomeResponse>() {
            @Override
            public void onResponse(Call<IncomeResponse> call, Response<IncomeResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                for (Annuity annuity : annuities
                        ) {
                    annuity.getAttach().clear();
                    adapter.notifyDataSetChanged();
                }
                LogUtils.d(TAG, "doSaveReview code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doSaveReview body: " + response.body().getDividends().toString());
                    LogUtils.d(TAG, " dividends image " + annuities.toString());
                    openFragment(R.id.layout_container, ReviewLumpSumFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
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


    public void AddIconAdd(Annuity dividend) {
        if (dividend.getImages() == null || dividend.getImages().size() == 0) {
            final Image image = new Image();
            image.setId(0);
            image.setAdd(true);
            dividend.getImages().add(image);
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
            case R.id.btn_next:
                if (isEditApp()) {
                    if (adapter.isExpend()) {
//                        for (Annuity o : annuities) {
//                            if ((o.getAttach().size() == 0 && o.getImages().size() < 2)
//                                    ) {
//                                DialogUtils.showOkDialog(getActivity(), getString(R.string.error), getString(R.string.required_all), getString(R.string.Yes), new AlertDialogOk.AlertDialogListener() {
//                                    @Override
//                                    public void onSubmit() {
//
//                                    }
//                                });
//                                return;
//                            }
//                        }
                        uploadImage(annuities);
                    } else {
                        doSaveReview();
                    }
                } else
                    openFragment(R.id.layout_container, ReviewLumpSumFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;
        }

    }
}
