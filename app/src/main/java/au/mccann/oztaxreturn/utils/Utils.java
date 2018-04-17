package au.mccann.oztaxreturn.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.io.UnsupportedEncodingException;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.view.TextViewCustom;

/**
 * Created by CanTran on 4/14/18.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

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
                .placeholder(R.drawable.image_placeholder)
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
                .placeholder(R.drawable.image_placeholder)
                .dontAnimate()
                .centerCrop()
                .into(img);
    }

    public static void displayImageRounded(Context context, ImageView img, String url, int radius, int margin) {

//        if (url == null) return;
        if (context == null) return;
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }

        Glide.with(context.getApplicationContext()).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.image_placeholder)
                .dontAnimate()
                .error(R.drawable.image_placeholder)
                .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, radius, margin))
                .into(img);
    }

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
                .placeholder(R.drawable.avatar_default)
                .dontAnimate()
                .into(img);
    }

    public static void showLongToast(Context context, String content, boolean isError, boolean isShort) {
        if (context == null) return;

        if (context instanceof Activity)
            if (((Activity) context).isFinishing()) {
                return;
            }

        showToastCustom(context, content, isError, isShort);
    }

    private static void showToastCustom(Context context, String content,
                                        boolean isError, boolean isShort) {
        Toast toastCustom = new Toast(context);
        ViewGroup viewGroup = (ViewGroup) toastCustom.getView();
        View viewToastCustom;
        if (isError) {
            viewToastCustom = LayoutInflater.from(context).inflate(
                    R.layout.toast_custom_warning, viewGroup);
        } else {
            viewToastCustom = LayoutInflater.from(context).inflate(
                    R.layout.toast_custom_info, viewGroup);
        }
        toastCustom.setDuration(isShort ? Toast.LENGTH_SHORT
                : Toast.LENGTH_LONG);
        toastCustom.setGravity(Gravity.BOTTOM, 0,
                (int) PxUtils.pxFromDp(context, context.getResources().getDimension(R.dimen.toast_offset)));
        toastCustom.setMargin(0, 0);
        toastCustom.setView(viewToastCustom);
        ((TextViewCustom) viewToastCustom.findViewById(R.id.toastDescription))
                .setText(content);
        toastCustom.show();
    }

    public static String getCurrentVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pInfo;

        try {
            pInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return "";

    }

    public static String base64(String input) {
        String outPut;
        byte[] data = new byte[0];
        try {
            data = input.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        outPut = Base64.encodeToString(data, Base64.DEFAULT);
        return outPut;
    }

    public static void sendSms(Context context, String phoneNumber, String content) {
        try {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", phoneNumber);
            smsIntent.putExtra("sms_body", content);
            context.startActivity(smsIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openPlayStore(Context context) {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


//    public static void showLongToast(Context context, String content, boolean isError, boolean isShort) {
//        if (context == null) return;
//
//        if (context instanceof Activity)
//            if (((Activity) context).isFinishing()) {
//                return;
//            }
//
//        showToastCustom(context, content, isError, isShort);
//    }

    //    private static void showToastCustom(Context context, String content,
//                                        boolean isError, boolean isShort) {
//        Toast toastCustom = new Toast(context);
//        ViewGroup viewGroup = (ViewGroup) toastCustom.getView();
//        View viewToastCustom;
//        if (isError) {
//            viewToastCustom = LayoutInflater.from(context).inflate(
//                    R.layout.toast_custom_warning, viewGroup);
//        } else {
//            viewToastCustom = LayoutInflater.from(context).inflate(
//                    R.layout.toast_custom_info, viewGroup);
//        }
//        toastCustom.setDuration(isShort ? Toast.LENGTH_SHORT
//                : Toast.LENGTH_LONG);
//        toastCustom.setGravity(Gravity.BOTTOM, 0,
//                (int) PxUtils.pxFromDp(context, context.getResources().getDimension(R.dimen.toast_offset)));
//        toastCustom.setMargin(0, 0);
//        toastCustom.setView(viewToastCustom);
//        ((TextViewHozo) viewToastCustom.findViewById(R.id.toastDescription))
//                .setText(content);
//        toastCustom.show();
//    }
    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidPhone(CharSequence phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }

    public static void hideKeyBoard(Activity context) {
        if (context == null) return;
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideSoftKeyboard(Context context, View EdittextHozo) {
        if (EdittextHozo == null)
            return;

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(EdittextHozo.getWindowToken(), 0);
    }


    public static void showSoftKeyboard(Context context, View EdittextHozo) {
        if (EdittextHozo == null)
            return;

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(EdittextHozo, 0);
    }

}
