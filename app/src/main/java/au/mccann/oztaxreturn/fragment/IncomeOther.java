package au.mccann.oztaxreturn.fragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ScrollView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.AlbumActivity;
import au.mccann.oztaxreturn.activity.PreviewImageActivity;
import au.mccann.oztaxreturn.adapter.ImageAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.dialog.PickImageDialog;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.model.ImageResponse;
import au.mccann.oztaxreturn.utils.FileUtils;
import au.mccann.oztaxreturn.utils.ImageUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.ExpandableLayout;
import au.mccann.oztaxreturn.view.MyGridView;
import au.mccann.oztaxreturn.view.RadioButtonCustom;

import static au.mccann.oztaxreturn.utils.TooltipUtils.showToolTipView;

/**
 * Created by LongBui on 4/17/18.
 */

public class IncomeOther extends BaseFragment implements View.OnClickListener {
    private static final String TAG = IncomeOther.class.getSimpleName();
    private MyGridView grImage;
    private ImageAdapter imageAdapter;
    private final ArrayList<Image> images = new ArrayList<>();
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private RadioButtonCustom rbYes, rbNo;
    private ExpandableLayout layout;
    private ScrollView scrollView;
    private Bundle bundle = new Bundle();
    private EdittextCustom edtResource;

    @Override
    protected int getLayout() {
        return R.layout.fragment_income_other;
    }

    @Override
    protected void initView() {
        grImage = (MyGridView) findViewById(R.id.gr_image);
        rbYes = (RadioButtonCustom) findViewById(R.id.rb_yes);
        rbNo = (RadioButtonCustom) findViewById(R.id.rb_no);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        edtResource = (EdittextCustom) findViewById(R.id.edt_resource);
        layout = (ExpandableLayout) findViewById(R.id.layout_expandable);
        findViewById(R.id.btn_next).setOnClickListener(this);
        underLineText(getString(R.string.income_radio_text_yes), 3, rbYes);
        underLineText(getString(R.string.income_radio_text_no), 2, rbNo);
    }

    @Override
    protected void initData() {
        bundle = getArguments();
        LogUtils.d(TAG, "initData bundle : " + bundle.toString());
        setTitle(getString(R.string.income_ws_title));
        appBarVisibility(false, true,0);
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


                } else {
                    layout.setExpanded(false);
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

    private void underLineText(String mystring, int n, RadioButtonCustom radioButtonCustom) {
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new StyleSpan(Typeface.BOLD), 0, n, 0);
        radioButtonCustom.setText(content);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (rbYes.isChecked()) {
                    if (edtResource.getText().toString().trim().isEmpty()) {
                        showToolTipView(getContext(), edtResource, Gravity.TOP, getString(R.string.valid_income_content), ContextCompat.getColor(getContext(), R.color.red));
                        return;
                    }
                    if (images.size() < 2) {
                        showToolTipView(getContext(), grImage, Gravity.TOP, getString(R.string.valid_deduction_image), ContextCompat.getColor(getContext(), R.color.red));
                        return;
                    }
                    ImageUtils.doUploadImage(getContext(), images, new ImageUtils.UpImagesListener() {
                        @Override
                        public void onSuccess(List<ImageResponse> responses) {
                            int[] imageArrIds = new int[responses.size()];
                            for (int i = 0; i < responses.size(); i++)
                                imageArrIds[i] = responses.get(i).getId();
                            bundle.putString(Constants.PARAMETER_INCOME_CONTENT, edtResource.getText().toString().trim());
                            bundle.putIntArray(Constants.PARAMETER_INCOME_CONTENT_ATTACHMENTS, imageArrIds);
                            openFragment(R.id.layout_container, Deduction.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
                        }
                    });

                } else
                    openFragment(R.id.layout_container, Deduction.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
                break;

        }
    }
}
