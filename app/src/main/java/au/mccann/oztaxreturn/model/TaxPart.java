package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by CanTran on 5/7/18.
 */
public class TaxPart {
    @SerializedName("on_taxable_income")
    private String onTaxableIncome;
    @SerializedName("medicare_levy")
    private String medicareLevy;
    @SerializedName("medicare_levy_surcharge")
    private String medicareLevySurcharge;
    @SerializedName("hecs_help_repayment")
    private String hecsHelpRepayment;
    @SerializedName("tax_offsets")
    private String taxOffsets;
    @SerializedName("tax_credits")
    private String taxCredits;

    public String getOnTaxableIncome() {
        return onTaxableIncome;
    }

    public void setOnTaxableIncome(String onTaxableIncome) {
        this.onTaxableIncome = onTaxableIncome;
    }

    public String getMedicareLevy() {
        return medicareLevy;
    }

    public void setMedicareLevy(String medicareLevy) {
        this.medicareLevy = medicareLevy;
    }

    public String getMedicareLevySurcharge() {
        return medicareLevySurcharge;
    }

    public void setMedicareLevySurcharge(String medicareLevySurcharge) {
        this.medicareLevySurcharge = medicareLevySurcharge;
    }

    public String getHecsHelpRepayment() {
        return hecsHelpRepayment;
    }

    public void setHecsHelpRepayment(String hecsHelpRepayment) {
        this.hecsHelpRepayment = hecsHelpRepayment;
    }

    public String getTaxOffsets() {
        return taxOffsets;
    }

    public void setTaxOffsets(String taxOffsets) {
        this.taxOffsets = taxOffsets;
    }

    public String getTaxCredits() {
        return taxCredits;
    }

    public void setTaxCredits(String taxCredits) {
        this.taxCredits = taxCredits;
    }

    @Override
    public String toString() {
        return "TaxPart{" +
                "onTaxableIncome='" + onTaxableIncome + '\'' +
                ", medicareLevy='" + medicareLevy + '\'' +
                ", medicareLevySurcharge='" + medicareLevySurcharge + '\'' +
                ", hecsHelpRepayment='" + hecsHelpRepayment + '\'' +
                ", taxOffsets='" + taxOffsets + '\'' +
                ", taxCredits='" + taxCredits + '\'' +
                '}';
    }
}
