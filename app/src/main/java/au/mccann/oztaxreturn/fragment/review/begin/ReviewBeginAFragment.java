package au.mccann.oztaxreturn.fragment.review.begin;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.fragment.BaseFragment;

/**
 * Created by LongBui on 4/23/18.
 */

public class ReviewBeginAFragment extends BaseFragment {
    @Override
    protected int getLayout() {
        return R.layout.review_begin_a_fragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.review_begin_a));
        appBarVisibility(false, true,1);
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }
}
