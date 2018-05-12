package au.mccann.oztaxreturn.fragment.review.begin;

import android.os.Bundle;
import android.view.View;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.fragment.BaseFragment;
import au.mccann.oztaxreturn.fragment.review.personal.ReviewPersonalInfomationA;
import au.mccann.oztaxreturn.utils.TransitionScreen;

/**
 * Created by LongBui on 4/23/18.
 */

public class ReviewBeginBFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = ReviewBeginBFragment.class.getSimpleName();

    @Override
    protected int getLayout() {
        return R.layout.review_begin_b_fragment;
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_review).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        getReviewProgress(getApplicationResponse());
        setTitle(getString(R.string.review_begin_a));
        appBarVisibility(true, true, 1);
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
