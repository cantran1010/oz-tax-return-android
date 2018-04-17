package au.mccann.oztaxreturn.fragment;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.AddNewBoardActivity;
import au.mccann.oztaxreturn.utils.TransitionScreen;

/**
 * Created by LongBui on 4/17/18.
 */

public class HomeFragment extends BaseFragment {
    @Override
    protected int getLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewBoardActivity.class);
                startActivity(intent, TransitionScreen.RIGHT_TO_LEFT);
            }
        });
    }

    @Override
    protected void resumeData() {

    }

    @Override
    public void onRefresh() {

    }
}
