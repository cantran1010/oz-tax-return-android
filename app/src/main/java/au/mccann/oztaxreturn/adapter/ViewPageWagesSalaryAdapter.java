package au.mccann.oztaxreturn.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.model.Job;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.TextViewCustom;

/**
 * Created by CanTran on 4/27/18.
 */
public class ViewPageWagesSalaryAdapter extends PagerAdapter {
    private static final String TAG = ViewPageWagesSalaryAdapter.class.getSimpleName();
    private final Context context;
    private List<Job> jobs;

    private RatingListener ratingListener;

    public interface RatingListener {
        void success();
    }

    public void setRatingListener(RatingListener ratingListener) {
        this.ratingListener = ratingListener;
    }

    public ViewPageWagesSalaryAdapter(Context context, List<Job> jobs) {
        this.context = context;
        this.jobs = jobs;
    }

    @Override
    public int getCount() {
        return jobs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final Job job = jobs.get(position);
        EdittextCustom edtGroos, edtTax, edtAllow, edtFringer, edtEmployer, edtCompanyName, edtCompanyAbn, edtCompanyContact;
        TextViewCustom tvJob;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View itemView = inflater.inflate(R.layout.item_job, container,
                false);
        edtGroos = itemView.findViewById(R.id.edt_gross);
        edtTax = itemView.findViewById(R.id.edt_tax);
        edtAllow = itemView.findViewById(R.id.edt_allow);
        edtFringer = itemView.findViewById(R.id.edt_reportable_fringer);
        edtEmployer = itemView.findViewById(R.id.edt_reportable_employer);
        edtCompanyName = itemView.findViewById(R.id.edt_company_name);
        edtCompanyAbn = itemView.findViewById(R.id.edt_company_ABN);
        edtCompanyContact = itemView.findViewById(R.id.edt_company_contact);
        tvJob = itemView.findViewById(R.id.tv_job);
        tvJob.setText(context.getString(R.string.job) + (position + 1));
        edtGroos.setText(job.getTotalGrossIncom());
        edtTax.setText(job.getTotalTaxWidthheld());
        edtAllow.setText(job.getAllowances());
        edtFringer.setText(job.getReporTableFringerBenefits());
        edtEmployer.setText(job.getReporTableEmployerSupper());
        edtCompanyName.setText(job.getCompanyName());
        edtCompanyAbn.setText(job.getCompanyAbn());
        edtCompanyContact.setText(job.getCompanyContact());
        if (job.isEdit()) {
            edtGroos.setEnabled(true);
            edtGroos.requestFocus();
            edtGroos.setSelection(edtGroos.length());
            edtTax.setEnabled(true);
            edtAllow.setEnabled(true);
            edtFringer.setEnabled(true);
            edtEmployer.setEnabled(true);
            edtCompanyName.setEnabled(true);
            edtCompanyAbn.setEnabled(true);
            edtCompanyContact.setEnabled(true);
        } else {
            tvJob.setEnabled(false);
            edtGroos.setEnabled(false);
            edtTax.setEnabled(false);
            edtAllow.setEnabled(false);
            edtFringer.setEnabled(false);
            edtEmployer.setEnabled(false);
            edtCompanyName.setEnabled(false);
            edtCompanyAbn.setEnabled(false);
            edtCompanyContact.setEnabled(false);
        }
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


}