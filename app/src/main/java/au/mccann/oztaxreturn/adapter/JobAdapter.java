package au.mccann.oztaxreturn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.model.Job;
import au.mccann.oztaxreturn.view.EdittextCustom;
import au.mccann.oztaxreturn.view.TextViewCustom;

/**
 * Created by cantran on 4/18/18.
 */

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.MyViewHolder> {
    private static final String TAG = JobAdapter.class.getName();
    private ArrayList<Job> jobs;
    private Context context;

    public JobAdapter(ArrayList<Job> jobs, Context context) {
        this.jobs = jobs;
        this.context = context;
    }

    public interface OnClickListener {
        public void onClick(int position);
    }

    private OnClickListener onClickListener;

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Job job = jobs.get(position);
        holder.tvJob.setText("job " + position);
        holder.edtGroos.setText(job.getTotalGrossIncom());
        holder.edtTax.setText(job.getTotalTaxWidthheld());
        holder.edtAllow.setText(job.getAllowances());
        holder.edtFringer.setText(job.getReporTableFringerBenefits());
        holder.edtEmployer.setText(job.getReporTableEmployerSupper());
        holder.edtCompanyName.setText(job.getCompanyName());
        holder.edtCompanyAbn.setText(job.getCompanyAbn());
        holder.edtCompanyContact.setText(job.getCompanyContact());
        if (job.isEdit()) {
            holder.edtGroos.setEnabled(true);
            holder.edtGroos.requestFocus();
            holder.edtTax.setEnabled(true);
            holder.edtAllow.setEnabled(true);
            holder.edtFringer.setEnabled(true);
            holder.edtEmployer.setEnabled(true);
            holder.edtCompanyName.setEnabled(true);
            holder.edtCompanyAbn.setEnabled(true);
            holder.edtCompanyContact.setEnabled(true);
        } else {
            holder.tvJob.setEnabled(false);
            holder.edtGroos.setEnabled(false);
            holder.edtTax.setEnabled(false);
            holder.edtAllow.setEnabled(false);
            holder.edtFringer.setEnabled(false);
            holder.edtEmployer.setEnabled(false);
            holder.edtCompanyName.setEnabled(false);
            holder.edtCompanyAbn.setEnabled(false);
            holder.edtCompanyContact.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        EdittextCustom edtGroos, edtTax, edtAllow, edtFringer, edtEmployer, edtCompanyName, edtCompanyAbn, edtCompanyContact;
        TextViewCustom tvJob;

        public MyViewHolder(View itemView) {
            super(itemView);
            edtGroos = itemView.findViewById(R.id.edt_gross);
            edtTax = itemView.findViewById(R.id.edt_tax);
            edtAllow = itemView.findViewById(R.id.edt_allow);
            edtFringer = itemView.findViewById(R.id.edt_reportable_fringer);
            edtEmployer = itemView.findViewById(R.id.edt_reportable_employer);
            edtCompanyName = itemView.findViewById(R.id.edt_company_name);
            edtCompanyAbn = itemView.findViewById(R.id.edt_company_ABN);
            edtCompanyContact = itemView.findViewById(R.id.edt_company_contact);
            tvJob = itemView.findViewById(R.id.tv_job);
        }
    }

}
