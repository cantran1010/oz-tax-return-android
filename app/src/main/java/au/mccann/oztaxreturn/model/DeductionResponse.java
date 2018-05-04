package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by CanTran on 5/4/18.
 */
public class DeductionResponse {
    private Vehicles vehicles;
    private Clothes clothes;
    private ArrayList<Education> educations;
    private ArrayList<OtherResponse> others;
    private ArrayList<Donation> donations;
    @SerializedName("tax_agents")
    private TaxAgents taxAgents;

    public Vehicles getVehicles() {
        return vehicles;
    }

    public void setVehicles(Vehicles vehicles) {
        this.vehicles = vehicles;
    }

    public Clothes getClothes() {
        return clothes;
    }

    public void setClothes(Clothes clothes) {
        this.clothes = clothes;
    }

    public ArrayList<Education> getEducations() {
        return educations;
    }

    public void setEducations(ArrayList<Education> educations) {
        this.educations = educations;
    }

    public ArrayList<OtherResponse> getOthers() {
        return others;
    }

    public void setOthers(ArrayList<OtherResponse> others) {
        this.others = others;
    }

    public ArrayList<Donation> getDonations() {
        return donations;
    }

    public void setDonations(ArrayList<Donation> donations) {
        this.donations = donations;
    }

    public TaxAgents getTaxAgents() {
        return taxAgents;
    }

    public void setTaxAgents(TaxAgents taxAgents) {
        this.taxAgents = taxAgents;
    }

    @Override
    public String toString() {
        return "DeductionResponse{" +
                "vehicles=" + vehicles +
                ", clothes=" + clothes +
                ", educations=" + educations +
                ", others=" + others +
                ", donations=" + donations +
                ", taxAgents=" + taxAgents +
                '}';
    }
}
