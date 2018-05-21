package au.mccann.oztaxreturn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.model.Job;
import au.mccann.oztaxreturn.view.EditTextEasyMoney;
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

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Job job = jobs.get(position);
        holder.tvJob.setText(context.getString(R.string.job) + (position + 1));
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
            holder.edtGroos.setSelection(holder.edtGroos.length());
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

        holder.edtGroos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                jobs.get(position).setTotalGrossIncom(holder.edtGroos.getValuesFloat());
            }
        });
        holder.edtTax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                jobs.get(position).setTotalTaxWidthheld(holder.edtTax.getValuesFloat());
            }
        });
        holder.edtAllow.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                jobs.get(position).setAllowances(holder.edtAllow.getValuesFloat());
            }
        });
        holder.edtFringer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                jobs.get(position).setReporTableFringerBenefits(holder.edtFringer.getValuesFloat());
            }
        });
        holder.edtEmployer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                jobs.get(position).setReporTableEmployerSupper(holder.edtEmployer.getValuesFloat());
            }
        });
        holder.edtCompanyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                jobs.get(position).setCompanyName(editable.toString().trim());
            }
        });
        holder.edtCompanyAbn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                jobs.get(position).setCompanyAbn(editable.toString().trim());
            }
        });
        holder.edtCompanyContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                jobs.get(position).setCompanyContact(editable.toString().trim());
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        EditTextEasyMoney edtGroos, edtTax, edtAllow, edtFringer, edtEmployer;
        EdittextCustom edtCompanyName, edtCompanyAbn, edtCompanyContact;
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
