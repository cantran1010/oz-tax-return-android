package au.mccann.oztaxreturn.activity;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.model.BlockResponse;
import au.mccann.oztaxreturn.view.TextViewCustom;

/**
 * Created by CanTran on 5/10/18.
 */
public class BlockActivity extends BaseActivity {
    private TextViewCustom tvContent;

    @Override
    protected int getLayout() {
        return R.layout.block_activity;
    }

    @Override
    protected void initView() {
        tvContent = findViewById(R.id.tv_content);
    }

    @Override
    protected void initData() {
        BlockResponse blockResponse = (BlockResponse) getIntent().getSerializableExtra(Constants.BLOCK_EXTRA);
        if (blockResponse.getStatus().equalsIgnoreCase(Constants.STATUS_USER_BLOCK)){
            tvContent.setText(getString(R.string.block_user));
        }else if (blockResponse.getStatus().equalsIgnoreCase(Constants.STATUS_USER_BLOCK)){
            tvContent.setText(getString(R.string.temporally_locked));
        }
    }

    @Override
    protected void resumeData() {

    }
}
