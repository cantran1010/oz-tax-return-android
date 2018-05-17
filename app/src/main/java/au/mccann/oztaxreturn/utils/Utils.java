package au.mccann.oztaxreturn.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
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
import com.github.florent37.viewtooltip.ViewTooltip;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.BaseActivity;
import au.mccann.oztaxreturn.activity.GeneralInfoActivity;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.model.Notification;
import au.mccann.oztaxreturn.rest.response.ApplicationResponse;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.TextViewCustom;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

import static au.mccann.oztaxreturn.networking.ApiClient.retrofit;

/**
 * Created by CanTran on 4/14/18.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    private static final int MAXSIZE = 2000;

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


    public static void showSoftKeyboard(Context context, View v) {
        if (v == null)
            return;

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, 0);
    }

    public static APIError parseError(Response<?> response) {
        Converter<ResponseBody, APIError> errorConverter = retrofit.responseBodyConverter(APIError.class, new Annotation[0]);

        @SuppressWarnings("UnusedAssignment") APIError error = new APIError();

        try {
            error = errorConverter.convert(response.errorBody());
        } catch (Exception e) {
            return new APIError();
        }

        return error;
    }

    public static List<String> getListYear() {
        List<String> listsYear = new ArrayList<>();

        int year = Calendar.getInstance().get(Calendar.YEAR);
        listsYear.add(String.valueOf(year - 1) + "-" + String.valueOf(year));
        listsYear.add(String.valueOf(year - 2 + "-" + String.valueOf(year - 1)));
        listsYear.add(String.valueOf(year - 3) + "-" + String.valueOf(year - 2));

        return listsYear;
    }

    public static File compressFile(File fileIn) {
        Bitmap bitmap;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(fileIn.getPath(), options);
        } catch (Exception e) {
            e.printStackTrace();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeFile(fileIn.getPath(), options);
        }

        LogUtils.d(TAG, "compressFile , width : " + bitmap.getWidth() + " , height : " + bitmap.getHeight());
        if (bitmap.getWidth() > MAXSIZE || bitmap.getHeight() > MAXSIZE) {
            float scale = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getWidth() / MAXSIZE : bitmap.getHeight() / MAXSIZE;
            try {
                File fileOut;
                // fix right orientation of image after capture

                ExifInterface exif = new ExifInterface(fileIn.getPath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                int angle = 0;

                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    angle = 90;
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    angle = 180;
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    angle = 270;
                }

                if (angle != 0) {
                    try {
                        Matrix mat = new Matrix();
                        mat.postRotate(angle);

                        //maybe out of memory when create bitmap so need scale bitmap before create and rotate bitmap
                        Bitmap bmpScale = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / scale), (int) (bitmap.getHeight() / scale), false);
                        bitmap = Bitmap.createBitmap(bmpScale, 0, 0, bmpScale.getWidth(), bmpScale.getHeight(), mat, true);

//                    Bitmap correctBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
//                    bitmap = Bitmap.createScaledBitmap(correctBmp, (int) (correctBmp.getWidth() / scale), (int) (correctBmp.getHeight() / scale), false);

                        bmpScale.recycle();
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }

                } else {
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / scale), (int) (bitmap.getHeight() / scale), false);
                }

                fileOut = new File(FileUtils.getInstance().getMaximyzDirectory(), "image" + System.currentTimeMillis() + ".jpg");
                Utils.compressBitmapToFile(bitmap, fileOut.getPath());

//                //recycle bitmap
//                bitmap.recycle();

                return fileOut;
            } catch (Exception e) {
                LogUtils.e(TAG, "compressFile , error : " + e.getMessage());
                return fileIn;
            }

        } else {
            if (!fileIn.getName().endsWith("jpg")) {
                try {
                    File fileOut;

                    // fix right orientation of image after capture
                    ExifInterface exif = new ExifInterface(fileIn.getPath());

                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    int angle = 0;

                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                        angle = 90;
                    } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                        angle = 180;
                    } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                        angle = 270;
                    }

                    if (angle != 0) {
                        Matrix mat = new Matrix();
                        mat.postRotate(angle);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
                    }

                    fileOut = new File(FileUtils.getInstance().getMaximyzDirectory(), "image" + System.currentTimeMillis() + ".jpg");
                    Utils.compressBitmapToFile(bitmap, fileOut.getPath());

//                    bitmap.recycle();
                    return fileOut;

                } catch (IOException e) {
                    e.printStackTrace();
                    return fileIn;
                }
            } else
                return fileIn;

        }
    }

    @SuppressWarnings("deprecation")
    public static void setViewBackground(View view, Drawable background) {
        if (view == null || background == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }


    public static void compressBitmapToFile(Bitmap bmp, String path) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bmp != null) bmp.recycle();
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap scaleBitmap(Bitmap bmInput, int maxsize) {
        if (bmInput.getWidth() > maxsize || bmInput.getHeight() > maxsize) {
            float scale = bmInput.getWidth() > bmInput.getHeight() ? bmInput.getWidth() / maxsize : bmInput.getHeight() / maxsize;
            return Bitmap.createScaledBitmap(bmInput, (int) (bmInput.getWidth() / scale), (int) (bmInput.getHeight() / scale), false);
        } else
            return bmInput;
    }

    public static String formatNumber2Digit(float input) {
        DecimalFormat myFormatter = new DecimalFormat("###,###.##");
        String result = myFormatter.format(input);
        if (result.endsWith(".0")) result = result.substring(0, result.length() - 3);
        return result;
    }

    static public String displayCurrency(String aFloat) {
        Float aFloat1 = Float.parseFloat(aFloat.replace(",", "").replace("$", ""));
        Float currencyAmount = new Float(aFloat);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormatter.format(currencyAmount);
    }
//
    public static String formatMoneyFloat(Context context, float input) {
        return context.getString(R.string.dolla) + formatNumber2Digit(input);
    }

    // format phone number to nation format
    public static String formatPhoneNumber(String phoneStr) {

        String result = "";
        String cCode = Locale.getDefault().getCountry();
        String lCode = Locale.getDefault().getLanguage();

        LogUtils.d(TAG, "getLocale , cCode : " + cCode);
        LogUtils.d(TAG, "getLocale , cCode : " + cCode);

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phone = phoneUtil.parse(phoneStr, Locale.getDefault().getCountry());
            result = phoneUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "formatPhoneNumber , result : " + result);
        return result;
    }

    public static String getContentNotification(Context context, Notification notification) {
        String result = "";

        ApplicationResponse applicationResponse = notification.getApplication();

        if (notification.getEvent().equals("admin_push")) {
            result = notification.getContent();
        } else if (notification.getEvent().equals("app_reviewed")) {
            result = context.getString(R.string.notification_reviewed);
        } else if (notification.getEvent().equals("app_lodged_ato")) {
            result = context.getString(R.string.notification_lodged_ato);
        } else if (notification.getEvent().equals("app_auditing_ato")) {
            result = context.getString(R.string.notification_auditing_ato);
        } else if (notification.getEvent().equals("app_completed")) {
            result = context.getString(R.string.notification_completed);
        } else if (notification.getEvent().equals("active_user")) {
            result = context.getString(R.string.notification_active_user);
        } else if (notification.getEvent().equals("deactivate_user")) {
            result = context.getString(R.string.notification_deactivate_user);
        } else if (notification.getEvent().equals("block_user")) {
            result = context.getString(R.string.notification_block_user);
        } else if (notification.getEvent().equals("message_received")) {
            result = notification.getContent();
        } else
            result = notification.getContent();

        return result;
    }

    public static void showKeyboard(Context context, EdittextCustom edittextCustom) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edittextCustom,
                InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showToolTip(Context context, View view, String content) {

        if (context instanceof Activity) {
            ViewTooltip
                    .on((Activity) context, view)
                    .autoHide(true, 2000)
                    .corner(30)
                    .position(ViewTooltip.Position.TOP)
                    .color(ContextCompat.getColor(view.getContext(), R.color.tool_tip))
                    .align(ViewTooltip.ALIGN.CENTER)
                    .text(content)
                    .show();
        } else {
            ViewTooltip
                    .on(view)
                    .autoHide(true, 2000)
                    .corner(30)
                    .position(ViewTooltip.Position.TOP)
                    .color(ContextCompat.getColor(view.getContext(), R.color.tool_tip))
                    .align(ViewTooltip.ALIGN.CENTER)
                    .text(content)
                    .show();
        }

    }

    public static String getCountryCode(Context context) {
        String locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            locale = context.getResources().getConfiguration().locale.getCountry();
        }
        return locale;
    }

    public static void openGeneralInfoActivity(Activity activity, String title, String url) {
        Intent intent = new Intent(activity, GeneralInfoActivity.class);
        intent.putExtra(Constants.URL_EXTRA, url);
        intent.putExtra(Constants.TITLE_INFO_EXTRA, title);
        if (activity instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) activity;
            baseActivity.startActivity(intent, TransitionScreen.RIGHT_TO_LEFT);
        }
    }
}
