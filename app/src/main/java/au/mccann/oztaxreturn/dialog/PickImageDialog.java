package au.mccann.oztaxreturn.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.view.ButtonCustom;


/**
 * Created by LongBui on 4/24/2017.
 */

public class PickImageDialog extends BaseDialog implements View.OnClickListener {

    public interface PickImageListener {
        void onCamera();

        void onGallery();
    }

    private PickImageListener pickImageListener;

    public void setPickImageListener(PickImageListener pickImageListener) {
        this.pickImageListener = pickImageListener;
    }

    public PickImageDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_layout_pick_image;
    }

    @Override
    protected void initData() {
        ButtonCustom btnCamera = findViewById(R.id.btn_camera);
        btnCamera.setOnClickListener(this);

        ButtonCustom btnGallery = findViewById(R.id.btn_gallery);
        btnGallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_camera:
                if (pickImageListener != null) pickImageListener.onCamera();
                hideView();
                break;

            case R.id.btn_gallery:
                if (pickImageListener != null) pickImageListener.onGallery();
                hideView();
                break;

        }
    }
}
