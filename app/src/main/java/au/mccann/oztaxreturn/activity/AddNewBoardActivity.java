package au.mccann.oztaxreturn.activity;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.adapter.OzSpinnerAdapter;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.dialog.AlertDialogOk;
import au.mccann.oztaxreturn.dialog.AlertDialogOkAndCancel;
import au.mccann.oztaxreturn.model.APIError;
import au.mccann.oztaxreturn.networking.ApiClient;
import au.mccann.oztaxreturn.rest.response.ApplicationResponse;
import au.mccann.oztaxreturn.utils.DialogUtils;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.ProgressDialogUtils;
import au.mccann.oztaxreturn.utils.Utils;
import au.mccann.oztaxreturn.view.ButtonCustom;
import au.mccann.oztaxreturn.view.CheckBoxCustom;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LongBui on 4/17/18.
 */

public class AddNewBoardActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = AddNewBoardActivity.class.getSimpleName();
    private LinearLayout layoutDuplicate, layoutCreateNew;
    private CheckBoxCustom cbDuplicate, cbCreateNew;
    private Spinner spYear, spName, spYearCreate;
    private ButtonCustom btnAdd;
    private EditText edtTaxPayer;
    private ArrayList<ApplicationResponse> applicationResponses;

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

        spYearCreate = findViewById(R.id.sp_year_create);

        findViewById(R.id.img_back).setOnClickListener(this);

        edtTaxPayer = findViewById(R.id.edt_tax_payer);
    }

    @Override
    protected void initData() {
        applicationResponses = getIntent().getParcelableArrayListExtra(Constants.APP_LIST_EXTRA);

        List<String> listName = new ArrayList<>();
        for (ApplicationResponse applicationResponse : applicationResponses) {
            if (!listName.contains(applicationResponse.getPayerName()))
                listName.add(applicationResponse.getPayerName());
        }
        OzSpinnerAdapter dataNameAdapter = new OzSpinnerAdapter(this, listName);
        spName.setAdapter(dataNameAdapter);

        List<String> listYear = new ArrayList<>();
        for (ApplicationResponse applicationResponse : applicationResponses) {
            if (!listYear.contains(applicationResponse.getFinancialYear()))
                listYear.add(applicationResponse.getFinancialYear());
        }
        OzSpinnerAdapter dataYearAdapter = new OzSpinnerAdapter(this, listYear);
        spYear.setAdapter(dataYearAdapter);

        OzSpinnerAdapter dataYearCreateAdapter = new OzSpinnerAdapter(this, Utils.getListYear());
        spYearCreate.setAdapter(dataYearCreateAdapter);

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

    private void doCreateApplication() {
        ProgressDialogUtils.showProgressDialog(this);
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("payer_name", edtTaxPayer.getText().toString().trim());
            jsonRequest.put("financial_year", spYearCreate.getSelectedItem().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtils.d(TAG, "doCreateApplication jsonRequest : " + jsonRequest.toString());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonRequest.toString());
        ApiClient.getApiService().createApplication(UserManager.getUserToken(), body).enqueue(new Callback<ApplicationResponse>() {
            @Override
            public void onResponse(Call<ApplicationResponse> call, Response<ApplicationResponse> response) {
                ProgressDialogUtils.dismissProgressDialog();
                LogUtils.d(TAG, "doCreateApplication code : " + response.code());

                if (response.code() == Constants.HTTP_CODE_OK) {
                    LogUtils.d(TAG, "doCreateApplication body : " + response.body().toString());
                    setResult(Constants.CREATE_APP_RESULT_CODE);
                    finish();
                } else {
                    APIError error = Utils.parseError(response);
                    LogUtils.d(TAG, "doCreateApplication error : " + error.message());
                    if (error != null) {
                        DialogUtils.showOkDialog(AddNewBoardActivity.this, getString(R.string.error), error.message(), getString(R.string.ok), new AlertDialogOk.AlertDialogListener() {
                            @Override
                            public void onSubmit() {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<ApplicationResponse> call, Throwable t) {
                LogUtils.e(TAG, "doCreateApplication onFailure : " + t.getMessage());
                ProgressDialogUtils.dismissProgressDialog();
                DialogUtils.showRetryDialog(AddNewBoardActivity.this, new AlertDialogOkAndCancel.AlertDialogListener() {
                    @Override
                    public void onSubmit() {
                        doCreateApplication();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    private void doAdd() {
        if (!cbCreateNew.isChecked() && !cbDuplicate.isChecked()) {
            Utils.showLongToast(this, getString(R.string.error_must_one), true, false);
            return;
        }

        if (cbCreateNew.isChecked())
            doCreateApplication();
        else if (cbDuplicate.isChecked()) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_back:
                finish();
                break;

            case R.id.btn_add:
                doAdd();
                break;

        }
    }
}
