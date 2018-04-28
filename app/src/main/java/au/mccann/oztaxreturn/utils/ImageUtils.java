package au.mccann.oztaxreturn.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.adapter.ImageAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Attachment;
import au.mccann.oztaxreturn.model.Image;
import au.mccann.oztaxreturn.networking.ApiClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CanTran on 4/14/18.
 */
public class ImageUtils {
    private final String TAG = ImageUtils.class.getName();

    public interface UpImagesListener {
        void onSuccess(List<Attachment> responses);
    }

    public void displayImage(Context context, ImageView img, String url) {
        LogUtils.d(TAG, "onBindViewHolder , url : " + url);
//        if (url == null) return;
        if (context == null) return;
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        Glide.with(context.getApplicationContext()).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_launcher)
                .dontAnimate()
                .into(img);
    }

    public static void displayImageCenterCrop(Context context, ImageView img, String url) {

//        if (url == null) return;
        if (context == null) return;
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }

        Glide.with(context.getApplicationContext()).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_launcher)
                .dontAnimate()
                .centerCrop()
                .into(img);
    }

//    public static void displayImageRounded(Context context, ImageView img, String url, int radius, int margin) {
//
////        if (url == null) return;
//        if (context == null) return;
//        if (context instanceof Activity) {
//            if (((Activity) context).isFinishing()) {
//                return;
//            }
//        }
//
//        Glide.with(context.getApplicationContext()).load(url)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.mipmap.ic_launcher)
//                .dontAnimate()
//                .error(R.mipmap.ic_launcher)
//                .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, radius, margin))
//                .into(img);
//    }

    public static void displayImageAvatar(Context context, ImageView img, String url) {

//        if (url == null) return;
        if (context == null) return;
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }

        Glide.with(context.getApplicationContext()).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_launcher)
                .dontAnimate()
                .into(img);
    }

    public static Bitmap scaleBitmap(Bitmap bmInput, int maxsize) {
        if (bmInput.getWidth() > maxsize || bmInput.getHeight() > maxsize) {
            float scale = bmInput.getWidth() > bmInput.getHeight() ? bmInput.getWidth() / maxsize : bmInput.getHeight() / maxsize;
            return Bitmap.createScaledBitmap(bmInput, (int) (bmInput.getWidth() / scale), (int) (bmInput.getHeight() / scale), false);
        } else
            return bmInput;
    }

    public static void doUploadImage(final Context context, final ArrayList<Image> images, final UpImagesListener upImagesListener) {
        ProgressDialogUtils.showProgressDialog(context);
//        File fileUp = Utils.compressFile(fileAttach);
//        LogUtils.d(TAG, "doAttachImage , file Name : " + fileUp.getName());
//        final RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), fileUp);
//        MultipartBody.Part itemPart = MultipartBody.Part.createFormData("image", fileUp.getName(), requestBody);
        LogUtils.d("", "doUploadImage onResponse images: " + images.size());
        List<MultipartBody.Part> parts = new ArrayList<>();
        // last image is "plus attach" , so realy size = size -1
        for (int i = 0; i < images.size(); i++)
            parts.add(MultipartBody.Part.createFormData("images[]", images.get(i).getName(), RequestBody.create(MediaType.parse("image/*"), new File(images.get(i).getPath()))));
        ApiClient.getApiService().uploadImage(UserManager.getUserToken(), parts).enqueue(new Callback<List<Attachment>>() {
            @Override
            public void onResponse(Call<List<Attachment>> call, Response<List<Attachment>> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d("", "doUploadImage onResponse : " + response.body());
                LogUtils.d("", "doUploadImage code : " + response.code());
                if (response.code() == Constants.HTTP_CODE_OK) {
                    upImagesListener.onSuccess(response.body());

                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d("", "doUploadImage error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(context, context.getString(R.string.error), error.message(), context.getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {
                            }
                        });
                    }
                }
                FileUtils.deleteDirectory(new File(FileUtils.OUTPUT_DIR));

            }

            @Override
            public void onFailure(Call<List<Attachment>> call, Throwable t) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.e("", "doUploadImage onFailure : " + t.getMessage());
                DialogUtils.showRetryDialog(context, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doUploadImage(context, images, upImagesListener);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                ProgressDialogUtils.dismissProgressDialog();
                FileUtils.deleteDirectory(new File(FileUtils.OUTPUT_DIR));
            }
        });
    }

    public static void showImage(List<Attachment> attachments, List<Image> images, ImageAdapter imageAdapter) {
        if (attachments.size() > 0) {
            for (Attachment attachment : attachments
                    ) {
                Image image = new Image();
                image.setId(attachment.getId());
                image.setAdd(false);
                image.setPath(attachment.getUrl());
                images.add(0, image);
            }
            imageAdapter.notifyDataSetChanged();
        }
    }

}
