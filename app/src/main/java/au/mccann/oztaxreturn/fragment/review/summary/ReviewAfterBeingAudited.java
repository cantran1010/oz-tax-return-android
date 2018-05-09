package au.mccann.oztaxreturn.fragment.review.summary;

import android.os.Bundle;
import android.view.View;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.fragment.review.personal.ReviewPersonalInfomationA;
import au.mccann.oztaxreturn.utils.TransitionScreen;

/**
 * Created by LongBui on 4/23/18.
 */

public class ReviewAfterBeingAudited extends BaseFragment implements View.OnClickListener {

    private static final String TAG = ReviewAfterBeingAudited.class.getSimpleName();

    @Override
    protected int getLayout() {
        return R.layout.review_after_being_audited;
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_review).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.Review_your_application));
        appBarVisibility(false, true, 1);
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_review:
                openFragment(R.id.layout_container, ReviewPersonalInfomationA.class, true, new Bundle(), TransitionScreen.RIGHT_TO_LEFT);
                break;
        }
    }
}
