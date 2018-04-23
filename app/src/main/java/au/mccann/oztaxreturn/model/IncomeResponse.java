package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by CanTran on 4/23/18.
 */
public class IncomeResponse {
    private int id;
    @SerializedName("jobs")
    private ArrayList<Job> jobs;
    @SerializedName("gov_payments")
    private GovPayment govPayment;
    @SerializedName("bank_interests")
    private Bank bank;
    @SerializedName("dividends")
    private ArrayList<Dividend> dividends;
    @SerializedName("etps")
    private Etps etps;
    @SerializedName("annuities")
    private ArrayList<Annuity> annuities;
    @SerializedName("rentals")
    private Rental rental;

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public void setJobs(ArrayList<Job> jobs) {
        this.jobs = jobs;
    }

    public GovPayment getGovPayment() {
        return govPayment;
    }

    public void setGovPayment(GovPayment govPayment) {
        this.govPayment = govPayment;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public ArrayList<Dividend> getDividends() {
        return dividends;
    }

    public void setDividends(ArrayList<Dividend> dividends) {
        this.dividends = dividends;
    }

    public Etps getEtps() {
        return etps;
    }

    public void setEtps(Etps etps) {
        this.etps = etps;
    }

    public ArrayList<Annuity> getAnnuities() {
        return annuities;
    }

    public void setAnnuities(ArrayList<Annuity> annuities) {
        this.annuities = annuities;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    @Override
    public String toString() {
        return "IncomeResponse{" +
                "jobs=" + jobs +
                ", govPayment=" + govPayment +
                ", bank=" + bank +
                ", dividends=" + dividends +
                ", etps=" + etps +
                ", annuities=" + annuities +
                ", rental=" + rental +
                '}';
    }
}
