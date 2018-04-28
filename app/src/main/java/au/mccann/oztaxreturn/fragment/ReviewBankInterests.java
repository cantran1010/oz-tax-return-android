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
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Bank;
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

import static au.mccann.oztaxreturn.utils.ImageUtils.showImage;
import static au.mccann.oztaxreturn.utils.TooltipUtils.showToolTipView;

/**
 * Created by CanTran on 4/24/18.
 */
public class ReviewBankInterests extends BaseFragment implements View.OnClickListener {
    private RadioButtonCustom rbYes, rbNo;
    private EdittextCustom edtBankName, edtBankNumber, edtTotalInteres, edtTax, edtBankFees;
    private MyGridView myGridView;
    private static final String TAG = IncomeOther.class.getSimpleName();
    private MyGridView grImage;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> images;
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private ScrollView scrollView;
    private ExpandableLayout layout;
    private Bank bank = new Bank();
    private ArrayList<Attachment> attach;
    private int appID;

    @Override
    protected int getLayout() {
        return R.layout.fragment_review_interest;
    }

    @Override
    protected void initView() {
        findViewById(R.id.fab).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        rbYes = (RadioButtonCustom) findViewById(R.id.rb_yes);
        rbYes.setEnabled(false);
        rbNo = (RadioButtonCustom) findViewById(R.id.rb_no);
        rbNo.setEnabled(false);
        edtBankName = (EdittextCustom) findViewById(R.id.edt_interest_bank_name);
        edtBankName.setEnabled(false);
        edtBankNumber = (EdittextCustom) findViewById(R.id.edt_interest_account_number);
        edtBankNumber.setEnabled(false);
        edtTotalInteres = (EdittextCustom) findViewById(R.id.edt_total_interests);
        edtTotalInteres.setEnabled(false);
        edtTax = (EdittextCustom) findViewById(R.id.edt_interest_tax);
        edtTax.setEnabled(false);
        edtBankFees = (EdittextCustom) findViewById(R.id.edt_interest_bank_fees);
        edtBankFees.setEnabled(false);
        myGridView = (MyGridView) findViewById(R.id.gr_image);
        myGridView.setEnabled(false);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        layout = (ExpandableLayout) findViewById(R.id.layout_expandable);
        grImage = (MyGridView) findViewById(R.id.gr_image);

    }

    @Override
    protected void initData() {
        images = new ArrayList<>();
        attach = new ArrayList<>();
        appID = getArguments().getInt(Constants.PARAMETER_APP_ID);
        setTitle(getString(R.string.review_income_title));
        appBarVisibility(true, true,0);
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
        getReviewIncome();
    }


    private void updateUI(Bank b) {
        rbYes.setChecked(b.isHad());
        edtBankName.setText(b.getBankName());
        edtBankNumber.setText(b.getAccountNumber());
        edtTotalInteres.setText(b.getTotalInterest());
        edtTax.setText(b.getTaxWithheld());
        edtBankFees.setText(b.getFees());
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

    private void getReviewIncome() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        LogUtils.d(TAG, "getReviewIncome code : " + appID);
        ApiClient.getApiService().getReviewIncome(UserManager.getUserToken(), appID).enqueue(new Callback<IncomeResponse>() {
            @Override
            public void onResponse(Call<IncomeResponse> call, Response<IncomeResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getReviewIncome code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    bank = response.body().getBank();
                    if (bank != null) updateUI(bank);
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
            govJson.put(Constants.PARAMETER_REVIEW_INCOME_GOVEMENT_HAD, rbYes.isChecked());
            govJson.put(Constants.PARAMETER_REVIEW_INCOME_BANK_NAME, edtBankName.getText().toString().trim());
            govJson.put(Constants.PARAMETER_REVIEW_INCOME_BANK_NUMBER, edtBankNumber.getText().toString().trim());
            govJson.put(Constants.PARAMETER_REVIEW_INCOME_BANK_TOTAL_INTEREST, edtTotalInteres.getText().toString().trim());
            govJson.put(Constants.PARAMETER_REVIEW_INCOME_BANK_TAX, edtTax.getText().toString().trim());
            govJson.put(Constants.PARAMETER_REVIEW_INCOME_BANK_FEES, edtBankFees.getText().toString().trim());
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
            jsonRequest.put(Constants.PARAMETER_REVIEW_INCOME_BANK, govJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "doSaveReview jsonRequest : " + jsonRequest.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().putReviewIncom(UserManager.getUserToken(), appID, body).enqueue(new Callback<IncomeResponse>() {
            @Override
            public void onResponse(Call<IncomeResponse> call, Response<IncomeResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "doSaveReview code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doSaveReview code: " + response.body().getJobs().toString());
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.PARAMETER_APP_ID, appID);
//                    openFragment(R.id.layout_container, GovementPayment.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
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
            case R.id.fab:
                edtBankName.requestFocus();
                edtBankName.setSelection(edtBankName.length());
                rbYes.setEnabled(true);
                rbNo.setEnabled(true);
                edtBankName.setEnabled(true);
                edtBankNumber.setEnabled(true);
                edtTotalInteres.setEnabled(true);
                edtTax.setEnabled(true);
                edtBankFees.setEnabled(true);
                myGridView.setEnabled(true);
                break;
            case R.id.btn_next:
                final Bundle bundle = new Bundle();
                if (rbYes.isChecked()) {
                    if (edtBankName.getText().toString().trim().isEmpty()) {
                        showToolTipView(getContext(), edtBankName, Gravity.TOP, getString(R.string.valid_app_bank_name), ContextCompat.getColor(getContext(), R.color.red));
                        return;
                    }
                    if (edtBankNumber.getText().toString().trim().isEmpty()) {
                        showToolTipView(getContext(), edtBankNumber, Gravity.TOP, getString(R.string.valid_app_account_number), ContextCompat.getColor(getContext(), R.color.red));
                        return;
                    }
                    if (edtTotalInteres.getText().toString().trim().isEmpty()) {
                        showToolTipView(getContext(), edtTotalInteres, Gravity.TOP, getString(R.string.valid_bank_total), ContextCompat.getColor(getContext(), R.color.red));
                        return;
                    }
                    if (edtBankFees.getText().toString().trim().isEmpty()) {
                        showToolTipView(getContext(), edtBankFees, Gravity.TOP, getString(R.string.valid_bank_fees), ContextCompat.getColor(getContext(), R.color.red));
                        return;
                    }
                    if (edtTax.getText().toString().trim().isEmpty()) {
                        showToolTipView(getContext(), edtTax, Gravity.TOP, getString(R.string.valid_bank_tax), ContextCompat.getColor(getContext(), R.color.red));
                        return;
                    }
                    if (images.size() < 2) {
                        showToolTipView(getContext(), grImage, Gravity.TOP, getString(R.string.valid_deduction_image), ContextCompat.getColor(getContext(), R.color.red));
                        return;
                    }
                    uploadImage();

                } else {
                    bundle.putInt(Constants.PARAMETER_APP_ID, appID);
//                    openFragment(R.id.layout_container, DeductionFragment.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
                }
                break;


        }

    }
}
