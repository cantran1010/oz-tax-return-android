package au.mccann.oztaxreturn.fragment.basic;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.view.TextViewCustom;

/**
 * Created by CanTran on 4/18/18.
 */
public class EstimateFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = EstimateFragment.class.getSimpleName();
    private TextViewCustom tvNote;



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
        appBarVisibility(false, true,0);
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
                openFragment(R.id.layout_container, PersonalFragment.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;
        }
    }
}
