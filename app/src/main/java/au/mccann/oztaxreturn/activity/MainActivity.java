package au.mccann.oztaxreturn.activity;

import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.utils.TransitionScreen;
import au.mccann.oztaxreturn.view.TextViewCustom;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private TextViewCustom tvTax;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        tvTax = findViewById(R.id.tv_tax_return);

    }

    @Override
    protected void initData() {
        setUnderLinePolicy(tvTax);
    }

    @Override
    protected void resumeData() {

    }

    private void setUnderLinePolicy(TextViewCustom textViewCustom) {
        String text = tvTax.getText().toString();
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);
        ssBuilder.setSpan(
                new ForegroundColorSpan(Color.parseColor("#f7be32")), // Span to add
                text.indexOf(getString(R.string.tax_return)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.tax_return)) + String.valueOf(getString(R.string.tax_return)).length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        textViewCustom.setText(ssBuilder);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class), TransitionScreen.FADE_IN);
                break;
            case R.id.btn_register:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class), TransitionScreen.RIGHT_TO_LEFT);
                break;
        }

    }
}