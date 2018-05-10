package au.mccann.oztaxreturn.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.AlbumActivity;
import au.mccann.oztaxreturn.adapter.MessageAdapter;
import au.mccann.oztaxreturn.adapter.OzSpinnerAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserEntity;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.dialog.PickImageDialog;
import au.mccann.oztaxreturn.listeners.EndlessRecyclerViewScrollListener;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.model.Message;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.Language;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.FileUtils;
import au.mccann.oztaxreturn.utils.ImageUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.EdittextCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = ContactFragment.class.getSimpleName();
    private RecyclerView rcvMsg;
    private EdittextCustom edtMsg;
    private ImageView imgAttach, imgSend;
    private ArrayList<Message> messages = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private static final int LIMIT = 20;
    private String since;
    private EndlessRecyclerViewScrollListener scrollListener;
    private final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String imgPath;
    private File fileAttach;
    private Spinner spLanguage;
    private ArrayList<Language> languagesArr = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initView() {
        rcvMsg = (RecyclerView) findViewById(R.id.rcv_msg);
        edtMsg = (EdittextCustom) findViewById(R.id.edt_msg);
        imgAttach = (ImageView) findViewById(R.id.img_attach);
        imgSend = (ImageView) findViewById(R.id.img_send);

        spLanguage = (Spinner) findViewById(R.id.sp_language);

        imgAttach.setOnClickListener(this);
        imgSend.setOnClickListener(this);
        setUpMessageList();
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.connect_title));
        appBarVisibility(true, false, 0);

        getMsg(null, LIMIT);
        getLanguage();

    }

    @Override
    protected void resumeData() {

    }

    public void onRefresh() {

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpMessageList() {
        LogUtils.d(TAG, "setUpMessageList start");
        messageAdapter = new MessageAdapter(messages, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(false);
        rcvMsg.setLayoutManager(linearLayoutManager);
        rcvMsg.setAdapter(messageAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page) {
                getMsg(since, LIMIT);
            }
        };
        rcvMsg.addOnScrollListener(scrollListener);
    }

    private void getMsg(final String s, final int limmit) {
        ProgressDialogUtils.showProgressDialog(getActivity());
        ApiClient.getApiService().getMsg(UserManager.getUserToken(), s, limmit).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                LogUtils.d(TAG, "getMsg code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getMsg body : " + response.body());

                    messages.addAll(response.body());
                    messageAdapter.notifyDataSetChanged();

                    if (response.body().size() > 1) {
                        since = response.body().get(response.body().size() - 1).getCreatedAt();
                    }
//                    else {
//                        scrollListener.onLoadMore();
//                    }
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getMsg error : " + error.message());
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
            public void onFailure(Call<List<Message>> call, Throwable t) {
                LogUtils.e(TAG, "getMsg onFailure : " + t.getMessage());
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getMsg(since, limmit);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                ProgressDialogUtils.dismissProgressDialog();
            }
        });
    }

    private void doSend(final boolean isMsg, final String msg) {

        if (isMsg && TextUtils.isEmpty(edtMsg.getText().toString().trim())) return;

//        ProgressDialogUtils.showProgressDialog(getActivity());
        JSONObject jsonRequest = new JSONObject();
        try {
            if (isMsg) jsonRequest.put("message", msg);
            else jsonRequest.put("attachment", Integer.valueOf(msg));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtils.d(TAG, "doSend jsonRequest : " + jsonRequest.toString());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().sendMsg(UserManager.getUserToken(), body).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                LogUtils.d(TAG, "doSend code : " + response.code());

                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doSend body : " + response.body().toString());
                    edtMsg.requestFocus();
                    edtMsg.setText("");
                    if (isMsg) Utils.showSoftKeyboard(getActivity(), edtMsg);
                    messages.add(0, response.body());
                    messageAdapter.notifyDataSetChanged();
                    rcvMsg.smoothScrollToPosition(0);
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "doSend error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }
//                ProgressDialogUtils.dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                LogUtils.e(TAG, "doSend onFailure : " + t.getMessage());
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doSend(isMsg, msg);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
//                ProgressDialogUtils.dismissProgressDialog();
            }
        });
    }

    private void doAttach() {
        checkPermissionImageAttach();
    }

    private void checkPermissionImageAttach() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) + ContextCompat
                .checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, Constants.PERMISSION_REQUEST_CODE);
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
                intent.putExtra(Constants.EXTRA_ONLY_IMAGE, true);
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
            imgPath = imagesSelected.get(0).getPath();
            fileAttach = new File(imgPath);
            doAttachImage();
        } else if (requestCode == Constants.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            fileAttach = new File(imgPath);
            doAttachImage();
        }
    }

    private void doAttachImage() {

        final ArrayList<Image> listUp = new ArrayList<>();
        Image image = new Image();
        image.setName(fileAttach.getName());
        image.setPath(fileAttach.getPath());
        listUp.add(image);

        ImageUtils.doUploadImage(getContext(), listUp, new ImageUtils.UpImagesListener() {
            @Override
            public void onSuccess(List<Attachment> responses) {
                doSend(false, String.valueOf(responses.get(responses.size() - 1).getId()));
            }
        });
    }

    private void getLanguage() {
        ProgressDialogUtils.showProgressDialog(getActivity());
        ApiClient.getApiService().getLanguage(UserManager.getUserToken()).enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(Call<List<Language>> call, Response<List<Language>> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getLanguage code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "getLanguage body : " + response.body().toString());

                    languagesArr = (ArrayList<Language>) response.body();

                    List<String> languages = new ArrayList<>();
                    for (Language language : response.body()) {
                        languages.add(language.getName());
                    }
                    OzSpinnerAdapter dataYearCreateAdapter = new OzSpinnerAdapter(getActivity(), languages);
                    spLanguage.setAdapter(dataYearCreateAdapter);

                    getUserInformation();
                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getLanguage error : " + error.message());
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Language>> call, Throwable t) {
                LogUtils.e(TAG, "getLanguage onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        getLanguage();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private void doUpdateLanguage() {
        JSONObject salaryJson = new JSONObject();
        try {
            salaryJson.put(Constants.PARAMETER_UPDATE_ACCOUNT_LANGUAGE, languagesArr.get(spLanguage.getSelectedItemPosition()).getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "doUpdateLanguage jsonRequest : " + salaryJson.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), salaryJson.toString());
        ApiClient.getApiService().updateUserInformation(UserManager.getUserToken(), body).enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                LogUtils.d(TAG, "doUpdateLanguage code: " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doUpdateLanguage boddy : " + response.body());
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "doUpdateLanguage error : " + error.toString());
                }

            }

            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {
                LogUtils.e(TAG, "updateUserInformation onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doUpdateLanguage();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private void getUserInformation() {
        LogUtils.d(TAG, "getUserInformation getUserToken : " + UserManager.getUserToken());
        ApiClient.getApiService().getUserInformation(UserManager.getUserToken()).enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, @NonNull Response<UserEntity> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "getUserInformation code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    UserEntity user = response.body();

                    int languageId = user.getLanguageId();

                    for (int i = 0; i < languagesArr.size(); i++) {
                        if (languageId == languagesArr.get(i).getId()) spLanguage.setSelection(i);
                    }

                    spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            doUpdateLanguage();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                } else {
                    APIError error = Utils.parseError(response);
                    if (error != null) {
                        LogUtils.d(TAG, "getUserInformation error : " + error.message());
                        DialogUtils.showOkDialog(getActivity(), getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
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
                DialogUtils.showRetryDialog(getActivity(), new AlertDialogOkAndCancel.AlertDialogListener() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_send:
                doSend(true, edtMsg.getText().toString().trim());
                break;

            case R.id.img_attach:
                doAttach();
                break;

        }
    }
}
