package au.mccann.oztaxreturn.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.view.RadioButtonCustom;

/**
 * Created by LongBui on 5/6/2017.
 */

public class CodeDialog extends BaseDialogFullScreenAnimDownUp implements View.OnClickListener {
    public interface CodeListenner {
        void onCodeListenner(String values);
    }

    private String values = "";
    private CodeListenner codeListenner;

    public CodeListenner getCodeListenner() {
        return codeListenner;
    }

    public void setCodeListenner(CodeListenner codeListenner) {
        this.codeListenner = codeListenner;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public CodeDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_code;
    }

    @Override
    protected void initData() {
        RadioButtonCustom rbR = findViewById(R.id.rb_r);
        rbR.setOnClickListener(this);
        RadioButtonCustom rbO = findViewById(R.id.rb_o);
        rbO.setOnClickListener(this);
        RadioButtonCustom rbD = findViewById(R.id.rb_d);
        rbD.setOnClickListener(this);
        RadioButtonCustom rbM = findViewById(R.id.rb_m);
        rbM.setOnClickListener(this);
        RadioButtonCustom rbS = findViewById(R.id.rb_s);
        rbS.setOnClickListener(this);
        RadioButtonCustom rbP = findViewById(R.id.rb_p);
        rbP.setOnClickListener(this);
        RadioButtonCustom rbN = findViewById(R.id.rb_n);
        rbN.setOnClickListener(this);
        rbR.setChecked(false);
        rbO.setChecked(false);
        rbD.setChecked(false);
        rbM.setChecked(false);
        rbN.setChecked(false);
        rbS.setChecked(false);
        rbP.setChecked(false);
        setCheck(values, rbD);
        setCheck(values, rbM);
        setCheck(values, rbN);
        setCheck(values, rbO);
        setCheck(values, rbP);
        setCheck(values, rbR);
        setCheck(values, rbS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_r:
            case R.id.rb_s:
            case R.id.rb_n:
            case R.id.rb_m:
            case R.id.rb_d:
            case R.id.rb_p:
            case R.id.rb_o:
                doDone((RadioButtonCustom) v);
                break;

        }
    }

    private void setCheck(String vl, RadioButtonCustom rb) {
        if (vl.equalsIgnoreCase(rb.getText().toString().substring(0, 1))) rb.setChecked(true);
    }

    private void doDone(RadioButtonCustom view) {
        if (codeListenner != null)
            codeListenner.onCodeListenner(view.getText().toString().substring(0, 1));
        hideView();
    }


}
