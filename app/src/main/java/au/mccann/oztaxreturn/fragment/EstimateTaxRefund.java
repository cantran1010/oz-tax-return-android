package au.mccann.oztaxreturn.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.view.TextViewCustom;

/**
 * Created by CanTran on 4/18/18.
 */
public class EstimateTaxRefund extends BaseFragment implements View.OnClickListener {
    private static final String TAG = EstimateTaxRefund.class.getSimpleName();
    private TextViewCustom tvNote;
    private Bundle bundle = new Bundle();

    @Override
    protected int getLayout() {
        return R.layout.fragment_estimate;
    }

    @Override
    protected void initView() {
        tvNote = (TextViewCustom) findViewById(R.id.tv_note);
        underLineText(getString(R.string.estimate_note));
        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.estimate_title));
        appBarVisibility(false, true);
        bundle = getArguments();
        LogUtils.d(TAG, "initData bundle : " + bundle.toString());
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    private void underLineText(String mystring) {
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, 0);
        tvNote.setText(content);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                openFragment(R.id.layout_container, PersonalInformation.class, true, bundle, TransitionScreen.RIGHT_TO_LEFT);
                break;
        }
    }
}
