package au.mccann.oztaxreturn.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import au.mccann.oztaxreturn.R;

/**
 * Created by CanTran on 6/15/17.
 */

public class CustomProgressDialog extends Dialog {
    public CustomProgressDialog(@NonNull Context context) {
        super(context, R.style.full_screen_dialog);
        setContentView(R.layout.dialog_fill);
        setCancelable(false);
    }

}
