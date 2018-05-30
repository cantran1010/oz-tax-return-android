package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by CanTran on 4/23/18.
 */
public class Job {
    private int id;
    @SerializedName("gross_income")
    private String totalGrossIncom;
    @SerializedName("tax_withheld")
    private String totalTaxWidthheld;
    @SerializedName("allowances")
    private String allowances;
    @SerializedName("fringe_benefits")
    private String reporTableFringerBenefits;
    @SerializedName("employer_con")
    private String reporTableEmployerSupper;
    @SerializedName("company_name")
    private String companyName;
    @SerializedName("company_abn")
    private String companyAbn;
    @SerializedName("company_contact")
    private String companyContact;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTotalGrossIncom() {
        return totalGrossIncom;
    }

    public void setTotalGrossIncom(String totalGrossIncom) {
        this.totalGrossIncom = totalGrossIncom;
    }

    public String getTotalTaxWidthheld() {
        return totalTaxWidthheld;
    }

    public void setTotalTaxWidthheld(String totalTaxWidthheld) {
        this.totalTaxWidthheld = totalTaxWidthheld;
    }

    public String getAllowances() {
        return allowances;
    }

    public void setAllowances(String allowances) {
        this.allowances = allowances;
    }

    public String getReporTableFringerBenefits() {
        return reporTableFringerBenefits;
    }

    public void setReporTableFringerBenefits(String reporTableFringerBenefits) {
        this.reporTableFringerBenefits = reporTableFringerBenefits;
    }

    public String getReporTableEmployerSupper() {
        return reporTableEmployerSupper;
    }

    public void setReporTableEmployerSupper(String reporTableEmployerSupper) {
        this.reporTableEmployerSupper = reporTableEmployerSupper;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAbn() {
        return companyAbn;
    }

    public void setCompanyAbn(String companyAbn) {
        this.companyAbn = companyAbn;
    }

    public String getCompanyContact() {
        return companyContact;
    }

    public void setCompanyContact(String companyContact) {
        this.companyContact = companyContact;
    }


    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", totalGrossIncom='" + totalGrossIncom + '\'' +
                ", totalTaxWidthheld='" + totalTaxWidthheld + '\'' +
                ", allowances='" + allowances + '\'' +
                ", reporTableFringerBenefits='" + reporTableFringerBenefits + '\'' +
                ", reporTableEmployerSupper='" + reporTableEmployerSupper + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyAbn='" + companyAbn + '\'' +
                ", companyContact='" + companyContact + '\'' +
                '}';
    }
}
