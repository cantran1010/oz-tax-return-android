package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by CanTran on 5/7/18.
 */
public class Summary {
    private IncomeSummary income;
    private DeductionSummary deduction;
    @SerializedName("tax_liability")
    private TaxSummary taxLiability;
    @SerializedName("tax_withheld")
    private String taxWithheld;
    @SerializedName("estimated_tax_refund")
    private String estimatedTaxRefund;
    @SerializedName("actual_tax_refund")
    private String actualTaxRefund;
    @SerializedName("attachment_url")
    private String attachmentUrl;
    private String status;

    public IncomeSummary getIncome() {
        return income;
    }

    public void setIncome(IncomeSummary income) {
        this.income = income;
    }

    public DeductionSummary getDeduction() {
        return deduction;
    }

    public void setDeduction(DeductionSummary deduction) {
        this.deduction = deduction;
    }

    public TaxSummary getTaxLiability() {
        return taxLiability;
    }

    public void setTaxLiability(TaxSummary taxLiability) {
        this.taxLiability = taxLiability;
    }

    public String getTaxWithheld() {
        return taxWithheld;
    }

    public void setTaxWithheld(String taxWithheld) {
        this.taxWithheld = taxWithheld;
    }

    public String getEstimatedTaxRefund() {
        return estimatedTaxRefund;
    }

    public void setEstimatedTaxRefund(String estimatedTaxRefund) {
        this.estimatedTaxRefund = estimatedTaxRefund;
    }

    public String getActualTaxRefund() {
        return actualTaxRefund;
    }

    public void setActualTaxRefund(String actualTaxRefund) {
        this.actualTaxRefund = actualTaxRefund;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Summary{" +
                "income=" + income +
                ", deduction=" + deduction +
                ", taxLiability=" + taxLiability +
                ", taxWithheld='" + taxWithheld + '\'' +
                ", estimatedTaxRefund='" + estimatedTaxRefund + '\'' +
                ", actualTaxRefund='" + actualTaxRefund + '\'' +
                ", attachmentUrl='" + attachmentUrl + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
