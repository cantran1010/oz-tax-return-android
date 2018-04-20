package au.mccann.oztaxreturn.fragment;

import android.view.View;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.TextViewCustom;

/**
 * Created by LongBui on 4/19/18.
 */

public class FirstCheckoutFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = FirstCheckoutFragment.class.getSimpleName();
    private TextViewCustom tvServiceFee, tvTotalFee;
    private EdittextCustom edtPromotionCode;


    @Override
    protected int getLayout() {
        return R.layout.first_checkout_fragment;
    }

    @Override
    protected void initView() {
        tvServiceFee = (TextViewCustom) findViewById(R.id.tv_service_fee);
        tvTotalFee = (TextViewCustom) findViewById(R.id.tv_total_fee);
        edtPromotionCode = (EdittextCustom) findViewById(R.id.edt_promotion_code);
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.first_checkout_title));
        appBarVisibility(false, true);
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }

    private void doNext() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_next:
                doNext();
                break;

        }
    }
}
