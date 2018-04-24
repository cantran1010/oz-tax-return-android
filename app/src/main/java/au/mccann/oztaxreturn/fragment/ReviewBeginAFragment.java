package au.mccann.oztaxreturn.fragment;

import au.mccann.oztaxreturn.R;

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
        appBarVisibility(false, true);
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }
}
