package au.mccann.oztaxreturn.activity;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.adapter.OzSpinnerAdapter;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.view.ButtonCustom;
import au.mccann.oztaxreturn.view.CheckBoxCustom;

/**
 * Created by LongBui on 4/17/18.
 */

public class AddNewBoardActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = AddNewBoardActivity.class.getSimpleName();
    private LinearLayout layoutDuplicate, layoutCreateNew;
    private CheckBoxCustom cbDuplicate, cbCreateNew;
    private Spinner spYear, spName;
    private ButtonCustom btnAdd;

    @Override
    protected int getLayout() {
        return R.layout.activity_add_new_board;
    }

    @Override
    protected void initView() {
        layoutDuplicate = findViewById(R.id.layout_duplicate);
        layoutCreateNew = findViewById(R.id.layout_create_new);

        cbDuplicate = findViewById(R.id.cb_duplicate);
        cbCreateNew = findViewById(R.id.cb_create_new);

        spName = findViewById(R.id.sp_name);
        spYear = findViewById(R.id.sp_year);

        btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);

        findViewById(R.id.img_back).setOnClickListener(this);

    }

    @Override
    protected void initData() {

        List<String> list = new ArrayList<>();
        list.add("name 1");
        list.add("name 2");
        list.add("name 3");
        list.add("name 4");

        OzSpinnerAdapter dataAdapter = new OzSpinnerAdapter(this, list);
        spName.setAdapter(dataAdapter);

        cbDuplicate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtils.d(TAG, "setOnCheckedChangeListener : " + b);
                if (b) {
                    cbCreateNew.setChecked(false);
                    layoutDuplicate.setVisibility(View.VISIBLE);
                    layoutCreateNew.setVisibility(View.GONE);
                } else {
                    layoutDuplicate.setVisibility(View.GONE);
                    layoutCreateNew.setVisibility(View.GONE);
                }
            }
        });

        cbCreateNew.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cbDuplicate.setChecked(false);
                    layoutDuplicate.setVisibility(View.GONE);
                    layoutCreateNew.setVisibility(View.VISIBLE);
                } else {
                    layoutDuplicate.setVisibility(View.GONE);
                    layoutCreateNew.setVisibility(View.GONE);
                }
            }
        });


    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_back:
                finish();
                break;

        }
    }
}
