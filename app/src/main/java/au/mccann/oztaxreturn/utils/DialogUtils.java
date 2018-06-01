package au.mccann.oztaxreturn.utils;

import android.app.Activity;
import android.content.Context;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.dialog.AlertDialogDownload;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.dialog.AlertDialogOkNonTouch;

/**
 * Created by LongBui on 4/4/2017.
 */
public class DialogUtils {

    public static void showRetryDialog(Context context, AlertDialogOkAndCancel.AlertDialogListener alertDialogListener) {
        if (context == null) return;
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        new AlertDialogOkAndCancel(context, context.getString(R.string.retry_title), context.getString(R.string.retry_content), context.getString(R.string.retry), context.getString(R.string.report_cancel), alertDialogListener);
    }

    public static void showOkAndCancelDialog(Context context, String title, String content, String submit, String cancel, AlertDialogOkAndCancel.AlertDialogListener alertDialogListener) {
        if (context == null) return;
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        new AlertDialogOkAndCancel(context, title, content, submit, cancel, alertDialogListener);
    }


    public static void showOkDialog(Context context, String title, String content, String submit, AlertDialogOk.AlertDialogListener alertDialogListener) {
        if (context == null) return;
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        new AlertDialogOk(context, title, content, submit, alertDialogListener);
    }

    public static void showReCommendUpdateDialog(Context context, AlertDialogOkAndCancel.AlertDialogListener alertDialogListener) {
        if (context == null) return;
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        new AlertDialogOkAndCancel(context, context.getString(R.string.update_title), context.getString(R.string.update_content), context.getString(R.string.oke), context.getString(R.string.report_cancel), alertDialogListener);
    }


    public static void showOkDialogNonTouch(Context context, String title, String content, String submit, AlertDialogOkNonTouch.AlertDialogListener alertDialogListener) {
        if (context == null) return;
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        new AlertDialogOkNonTouch(context, title, content, submit, alertDialogListener);
    }

    public static void showDownloadDialog(Context context, String url, AlertDialogDownload.AlertDialogListener alertDialogListener) {
        if (context == null) return;
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        new AlertDialogDownload(context, url, alertDialogListener);
    }

}
