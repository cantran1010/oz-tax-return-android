package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by CanTran on 5/7/18.
 */
public class DeductionPart {
    private String vehicles;
    private String clothes;
    private String educations;
    private String others;
    private String donations;
    @SerializedName("tax_agents")
    private String taxAgents;
    @SerializedName("bank_fees")
    private String bankFees;

    public String getVehicles() {
        return vehicles;
    }

    public void setVehicles(String vehicles) {
        this.vehicles = vehicles;
    }

    public String getClothes() {
        return clothes;
    }

    public void setClothes(String clothes) {
        this.clothes = clothes;
    }

    public String getEducations() {
        return educations;
    }

    public void setEducations(String educations) {
        this.educations = educations;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getDonations() {
        return donations;
    }

    public void setDonations(String donations) {
        this.donations = donations;
    }

    public String getTaxAgents() {
        return taxAgents;
    }

    public void setTaxAgents(String taxAgents) {
        this.taxAgents = taxAgents;
    }

    public String getBankFees() {
        return bankFees;
    }

    public void setBankFees(String bankFees) {
        this.bankFees = bankFees;
    }

    @Override
    public String toString() {
        return "DeductionPart{" +
                "vehicles='" + vehicles + '\'' +
                ", clothes='" + clothes + '\'' +
                ", educations='" + educations + '\'' +
                ", others='" + others + '\'' +
                ", donations='" + donations + '\'' +
                ", taxAgents='" + taxAgents + '\'' +
                ", bankFees='" + bankFees + '\'' +
                '}';
    }
}
