package au.mccann.oztaxreturn.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.adapter.OzSpinnerAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserEntity;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.dialog.PickImageDialog;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Attachment;
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
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static au.mccann.oztaxreturn.common.Constants.REQUEST_CODE_PICK_IMAGE;
import static au.mccann.oztaxreturn.common.Constants.RESPONSE_CODE_PICK_IMAGE;
import static au.mccann.oztaxreturn.utils.DateTimeUtils.getDateBirthDayFromIso;
import static au.mccann.oztaxreturn.utils.Utils.showToolTip;


/**
 * Created by CanTran on 5/9/18.
 */
public class ManageAccount extends BaseActivity implements View.OnClickListener {
    private final static String TAG = ManageAccount.class.getName();
    private CircleImageView imgAvatar;
    private EdittextCustom edtTitle, edtFirstname, edtMidname, edtLastName, edtBirthDay, edtStreetNumber, edtSuburb, edtState, edtPostCode;
    private EdittextCustom edtUserName, edtOldPassWord, edtNewPassWord, edtNewPassWordAgain;
    private Spinner spGender;
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath = "";
    private Image image;
    private Calendar calendar = GregorianCalendar.getInstance();
    private List<String> genders = new ArrayList<>();
    private int imgID;

    @Override
    protected int getLayout() {
        return R.layout.activity_mannager_account;
    }

    @Override
    protected void initView() {
        imgAvatar = findViewById(R.id.img_user_avatar);
        edtTitle = findViewById(R.id.edt_title);
        spGender = findViewById(R.id.sp_gender);
        edtFirstname = findViewById(R.id.edt_first_name);
        edtMidname = findViewById(R.id.edt_middle_name);
        edtLastName = findViewById(R.id.edt_last_name);
        edtBirthDay = findViewById(R.id.edt_birthday);
        edtStreetNumber = findViewById(R.id.edt_street_name);
        edtSuburb = findViewById(R.id.edt_suburb);
        edtState = findViewById(R.id.edt_state);
        edtPostCode = findViewById(R.id.edt_post_code);

        edtUserName = findViewById(R.id.edt_user_name);
        edtOldPassWord = findViewById(R.id.edt_old_password);
        edtNewPassWord = findViewById(R.id.edt_new_password);
        edtNewPassWordAgain = findViewById(R.id.edt_new_password_again);
        imgAvatar.setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
        edtBirthDay.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        genders = Arrays.asList(getResources().getStringArray(R.array.string_array_gender));
        OzSpinnerAdapter dataNameAdapter = new OzSpinnerAdapter(this, genders);
        spGender.setAdapter(dataNameAdapter);
        updateUI(UserManager.getUserEntity());
        getUserInformation();
    }

    @Override
    protected void resumeData() {

    }

    private void openDatePicker() {
        LogUtils.d(TAG, "openDatePicker" + DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));
        @SuppressWarnings("deprecation") DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        if (view.isShown()) {
                            calendar.set(year, monthOfYear, dayOfMonth);
                            edtBirthDay.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()));
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    private void updateUI(UserEntity body) {
        if (body.getAvatar() != null) {
            Utils.displayImage(ManageAccount.this, imgAvatar, body.getAvatar().getUrl());
            imgID = Integer.parseInt(body.getAvatar().getId());
        }
        edtTitle.setText(body.getTitle());
        for (int i = 0; i < genders.size(); i++) {
            if (body.getGender().equalsIgnoreCase(genders.get(i))) {
                spGender.setSelection(i);
                return;
            }
        }
        edtFirstname.setText(body.getFirstName());
        edtMidname.setText(body.getMiddleName());
        edtLastName.setText(body.getLastName());
        if (body.getBirthday() != null && !body.getBirthday().isEmpty())
            edtBirthDay.setText(getDateBirthDayFromIso(body.getBirthday()));
        edtStreetNumber.setText(body.getStreet());
        edtSuburb.setText(body.getSuburb());
        edtState.setText(body.getState());
        edtPostCode.setText(body.getPostCode());
        edtUserName.setText(body.getUserName());
    }

    private void getUserInformation() {
        ProgressDialogUtils.showProgressDialog(this);
        LogUtils.d(TAG, "getUserInformation getUserToken : " + UserManager.getUserToken());
        ApiClient.getApiService().getUserInformation(UserManager.getUserToken()).enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, @NonNull Response<UserEntity> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getUserInformation code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    UserEntity user = response.body();
                    user.setToken(UserManager.getUserToken());
                    UserManager.insertUser(user);
                    LogUtils.d(TAG, "getUserInformation body : " + UserManager.getUserEntity().toString());
                    updateUI(UserManager.getUserEntity());
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getUserInformation error : " + error.message());
                        DialogUtils.showOkDialog(ManageAccount.this, getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<UserEntity> call, @NonNull Throwable t) {
                LogUtils.e(TAG, "getUserInformation onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(ManageAccount.this, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getUserInformation();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private String getImgPath() {
        return imgPath;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_REQUEST_CODE_AVATA);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        PickImageDialog pickImageDialog = new PickImageDialog(this);
        pickImageDialog.setPickImageListener(new PickImageDialog.PickImageListener() {
            @Override
            public void onCamera() {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                startActivityForResult(cameraIntent, Constants.REQUEST_CODE_CAMERA);
            }

            @Override
            public void onGallery() {
                Intent intent = new Intent(ManageAccount.this, AlbumActivity.class);
                intent.putExtra(Constants.EXTRA_ONLY_IMAGE, true);
                startActivityForResult(intent, Constants.REQUEST_CODE_PICK_IMAGE, TransitionScreen.RIGHT_TO_LEFT);
            }
        });
        pickImageDialog.showView();
    }

    private Uri setImageUri() {
        @SuppressWarnings("AccessStaticViaInstance") File file = new File(FileUtils.getInstance().getMaximyzDirectory(), "image" + System.currentTimeMillis() + ".jpg");
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
                openCamera();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE
                && resultCode == RESPONSE_CODE_PICK_IMAGE
                && data != null) {
            ArrayList<Image> imagesSelected = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            image = new Image();
            image.setName(getString(R.string.app_name));
            image.setAdd(false);
            imgPath = imagesSelected.get(0).getPath();
            image.setPath(imgPath);
            Utils.displayImage(ManageAccount.this, imgAvatar, imgPath);
        } else if (requestCode == Constants.REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            imgPath = getImgPath();
            image = new Image();
            image.setName(getString(R.string.app_name));
            image.setAdd(false);
            image.setPath(imgPath);
            Utils.displayImage(ManageAccount.this, imgAvatar, imgPath);
        }
    }

    private void uploadImage() {
        final ArrayList<Image> listUp = new ArrayList<>();
        listUp.add(image);
        LogUtils.d(TAG, "uploadImage" + listUp.toString());
        ImageUtils.doUploadImage(this, listUp, new ImageUtils.UpImagesListener() {
            @Override
            public void onSuccess(List<Attachment> responses) {
                imgID = responses.get(0).getId();
                doSave();
            }
        });
    }

    private void save() {
        if (edtTitle.getText().toString().trim().isEmpty()) {
            showToolTip(this, edtTitle, getString(R.string.vali_all_empty));
            edtTitle.requestFocus();
            edtTitle.getParent().requestChildFocus(edtTitle, edtTitle);
            return;
        }
        if (edtFirstname.getText().toString().trim().isEmpty()) {
            showToolTip(this, edtFirstname, getString(R.string.vali_all_empty));
            edtFirstname.requestFocus();
            edtFirstname.getParent().requestChildFocus(edtFirstname, edtFirstname);
            return;
        }
        if (edtMidname.getText().toString().trim().isEmpty()) {
            showToolTip(this, edtMidname, getString(R.string.vali_all_empty));
            edtMidname.requestFocus();
            edtMidname.getParent().requestChildFocus(edtMidname, edtMidname);
            return;
        }
        if (edtLastName.getText().toString().trim().isEmpty()) {
            showToolTip(this, edtLastName, getString(R.string.vali_all_empty));
            edtLastName.requestFocus();
            edtLastName.getParent().requestChildFocus(edtLastName, edtLastName);
            return;
        }
        if (edtBirthDay.getText().toString().trim().isEmpty()) {
            showToolTip(this, edtBirthDay, getString(R.string.vali_all_empty));
            edtBirthDay.requestFocus();
            edtBirthDay.getParent().requestChildFocus(edtBirthDay, edtBirthDay);
            return;
        }
        if (edtStreetNumber.getText().toString().trim().isEmpty()) {
            showToolTip(this, edtStreetNumber, getString(R.string.vali_all_empty));
            edtStreetNumber.requestFocus();
            edtStreetNumber.getParent().requestChildFocus(edtStreetNumber, edtStreetNumber);
            return;
        }
        if (edtSuburb.getText().toString().trim().isEmpty()) {
            showToolTip(this, edtSuburb, getString(R.string.vali_all_empty));
            edtSuburb.requestFocus();
            edtSuburb.getParent().requestChildFocus(edtSuburb, edtSuburb);
            return;
        }
        if (edtPostCode.getText().toString().trim().isEmpty()) {
            showToolTip(this, edtPostCode, getString(R.string.vali_all_empty));
            edtPostCode.requestFocus();
            edtPostCode.getParent().requestChildFocus(edtPostCode, edtPostCode);
            return;
        }
        if (edtState.getText().toString().trim().isEmpty()) {
            showToolTip(this, edtState, getString(R.string.vali_all_empty));
            edtState.requestFocus();
            edtState.getParent().requestChildFocus(edtState, edtState);
            return;
        }
//        if (edtOldPassWord.getText().toString().trim().isEmpty()) {
//            showToolTip(this, edtOldPassWord, Gravity.TOP, getString(R.string.vali_all_empty),
//                    ContextCompat.getColor(this, R.color.red));
//            edtOldPassWord.requestFocus();
//            edtOldPassWord.getParent().requestChildFocus(edtOldPassWord, edtOldPassWord);
//            return;
//        }
        if (0 < edtNewPassWordAgain.getText().toString().length() && edtOldPassWord.getText().toString().trim().length() < 5) {
            showToolTip(this, edtNewPassWordAgain, getString(R.string.vali_password_lenth));
            edtNewPassWordAgain.requestFocus();
            edtNewPassWordAgain.getParent().requestChildFocus(edtNewPassWordAgain, edtNewPassWordAgain);
            return;
        }
        if (!imgPath.isEmpty()) uploadImage();
        else
            doSave();
    }

    private void doSave() {
        ProgressDialogUtils.showProgressDialog(ManageAccount.this);
        JSONObject salaryJson = new JSONObject();
        try {
            salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_TITLE, edtTitle.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_FIRST_NAME, edtFirstname.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_MIDDLE_NAME, edtMidname.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_LAST_NAME, edtLastName.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_BIRTHDAY, edtBirthDay.getText().toString().trim());
            if (imgID != 0) salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_AVATAR, imgID);
            salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_GENDER, spGender.getSelectedItem().toString());
            salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_STREET, edtStreetNumber.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_SUBURB, edtSuburb.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_POST_CODE, edtPostCode.getText().toString().trim());
            salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_POST_STATE, edtState.getText().toString());
            salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_PASSWORD, edtOldPassWord.getText());
            if (edtNewPassWord.getText().toString().trim().length() > 0) {
                salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_NEW_PASSWORD, edtNewPassWord.getText().toString().trim());
                salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_NEW_PASSWORD, edtNewPassWordAgain.getText().toString().trim());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "updateUserInformation jsonRequest : " + salaryJson.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), salaryJson.toString());
        ApiClient.getApiService().updateUserInformation(UserManager.getUserToken(), body).enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "updateUserInformation code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    UserEntity user = response.body();
                    user.setToken(UserManager.getUserToken());
                    UserManager.insertUser(user);
                    finish();
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.e(TAG, "updateUserInformation error : " + error.message());
                    if (error != null) {
                        if (error.status().equalsIgnoreCase(getString(R.string.password_incorrect))) {
                            if (edtOldPassWord.getText().toString().trim().isEmpty()) {
                                showToolTip(ManageAccount.this, edtOldPassWord, error.message());
                                edtOldPassWord.requestFocus();
                                edtOldPassWord.getParent().requestChildFocus(edtOldPassWord, edtOldPassWord);
                            }
                        } else if (error.status().equalsIgnoreCase(getString(R.string.password_confirm))) {
                            if (edtNewPassWord.getText().toString().trim().isEmpty()) {
                                showToolTip(ManageAccount.this, edtNewPassWord, error.message());
                                edtNewPassWord.requestFocus();
                                edtNewPassWord.getParent().requestChildFocus(edtNewPassWord, edtNewPassWord);
                            }
                        } else
                            DialogUtils.showOkDialog(ManageAccount.this, getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                                @Override
                                public void onSubmit() {

                                }
                            });
                    }
                }

            }

            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {
                LogUtils.e(TAG, "updateUserInformation onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(ManageAccount.this, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doSave();
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
            case R.id.img_user_avatar:
                checkPermission();
                break;
            case R.id.btn_save:
                save();
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.edt_birthday:
                openDatePicker();
                break;
        }
    }


}
