package au.mccann.oztaxreturn.utils;

import android.app.Activity;
import android.content.Context;

import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;

/**
 * Created by LongBui on 4/4/2017.
 */
public class DialogUtils {

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


}
