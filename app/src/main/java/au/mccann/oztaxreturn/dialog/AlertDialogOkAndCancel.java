package au.mccann.oztaxreturn.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.view.TextViewCustom;

/**
 * Created by LongBui on 5/8/2017.
 */

public class AlertDialogOkAndCancel extends BaseDialog implements View.OnClickListener {

    private final String title;
    private final String content;
    private final String submit;
    private final String cancel;

    public interface AlertDialogListener {
        void onSubmit();

        void onCancel();
    }

    private final AlertDialogListener alertDialogListener;

    public AlertDialogOkAndCancel(@NonNull Context context, String title, String content, String submit, String cancel, AlertDialogListener alertDialogListener) {
        super(context);
        this.title = title;
        this.content = content;
        this.submit = submit;
        this.cancel = cancel;
        this.alertDialogListener = alertDialogListener;

        showView();
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_alert_ok_and_cancel;
    }

    @Override
    protected void initData() {

        TextViewCustom tvSubmit = findViewById(R.id.tv_yes);
        tvSubmit.setOnClickListener(this);

        TextViewCustom tvCancel = findViewById(R.id.tv_no);
        tvCancel.setOnClickListener(this);

        TextViewCustom tvTitle = findViewById(R.id.tv_title);
        TextViewCustom tvContent = findViewById(R.id.tv_content);

        tvTitle.setText(title);
        tvContent.setText(content);
        tvSubmit.setText(submit);
        tvCancel.setText(cancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_yes:
                if (alertDialogListener != null) alertDialogListener.onSubmit();
                hideView();
                break;

            case R.id.tv_no:
                if (alertDialogListener != null) alertDialogListener.onCancel();
                hideView();
                break;
        }
    }
}
