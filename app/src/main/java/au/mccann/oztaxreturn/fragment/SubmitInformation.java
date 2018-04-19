package au.mccann.oztaxreturn.fragment;

import au.mccann.oztaxreturn.R;

/**
 * Created by CanTran on 4/19/18.
 */
public class SubmitInformation extends BaseFragment {
    @Override
    protected int getLayout() {
        return R.layout.fragment_personal_submit;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.personal_information_title));
        appBarVisibility(false, true);

    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }
}
