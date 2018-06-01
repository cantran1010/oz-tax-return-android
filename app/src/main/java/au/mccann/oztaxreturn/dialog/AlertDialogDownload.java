package au.mccann.oztaxreturn.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.view.ButtonCustom;

/**
 * Created by LongBui on 5/8/2017.
 */

public class AlertDialogDownload extends BaseDialog implements View.OnClickListener {

    private final String url;

    public interface AlertDialogListener {
        void onDownload();

        void onView();
    }

    private final AlertDialogListener alertDialogListener;

    public AlertDialogDownload(@NonNull Context context, String url, AlertDialogListener alertDialogListener) {
        super(context);
        this.url = url;
        this.alertDialogListener = alertDialogListener;
        showView();
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_alert_download;
    }

    @Override
    protected void initData() {
        ButtonCustom tvSubmit = findViewById(R.id.btn_download);
        tvSubmit.setOnClickListener(this);
        ButtonCustom tvCancel = findViewById(R.id.btn_view);
        tvCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download:
                if (alertDialogListener != null) alertDialogListener.onDownload();
                break;

            case R.id.btn_view:
                if (alertDialogListener != null) alertDialogListener.onView();
                hideView();
//                File pdfFile = new File(Environment.getExternalStorageDirectory() + "/testthreepdf/" + "maven.pdf");  // -> filename = maven.pdf
//                Uri path = Uri.fromFile(pdfFile);
//                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//                pdfIntent.setDataAndType(path, "application/pdf");
//                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                try {
//                    getContext().startActivity(pdfIntent);
//                } catch (ActivityNotFoundException e) {
//                    Toast.makeText(getContext(), "No Application available to view PDF", Toast.LENGTH_SHORT).show();
//                }
                break;
        }
    }
}
