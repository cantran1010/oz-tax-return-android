package au.mccann.oztaxreturn.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import au.mccann.oztaxreturn.R;

/**
 * Created by CanTran on 4/14/18.
 */
public class ImageUtils {
    private static final String TAG = ImageUtils.class.getName();

    public static void displayImage(Context context, ImageView img, String url) {
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

}
