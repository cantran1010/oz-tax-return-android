package au.mccann.oztaxreturn.rest.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LongBui on 5/12/18.
 */
public class ReviewProgressResponse {
    @SerializedName("info_name_residency")
    public boolean infoNameResidency;
    @SerializedName("info_bank_address")
    public boolean infoBankAddress;
    @SerializedName("info_education_debt")
    public boolean infoEducationDebt;
    @SerializedName("income_wages_salary")
    public boolean incomeWagesSalary;
    @SerializedName("income_gov_payments")
    public boolean incomeGovPayments;
    @SerializedName("income_bank_interests")
    public boolean incomeBankInterests;
    @SerializedName("income_dividends")
    public boolean incomeDividends;
    @SerializedName("income_etps")
    public boolean incomeEtps;
    @SerializedName("income_annuities")
    public boolean incomeAnnuities;
    @SerializedName("income_lump_sums")
    public boolean incomeLumpSums;
    @SerializedName("income_rentals")
    public boolean incomeRentals;
    @SerializedName("deduction_vehicles")
    public boolean deductionVehicles;
    @SerializedName("deduction_clothes")
    public boolean deductionClothes;
    @SerializedName("deduction_educations")
    public boolean deductionEducations;
    @SerializedName("deduction_others")
    public boolean deductionOthers;
    @SerializedName("deduction_donations")
    public boolean deductionDonations;
    @SerializedName("deduction_tax_agents")
    public boolean deductionTaxAgents;
    @SerializedName("health_dependants")
    public boolean healthDependants;
    @SerializedName("health_medicares")
    public boolean healthMedicares;
    @SerializedName("health_privates")
    public boolean healthPrivates;
    @SerializedName("health_spouses")
    public boolean healthSpouses;
    @SerializedName("total")
    public int total;
    @SerializedName("complete")
    public int complete;
    @SerializedName("percent")
    public int percent;

    public boolean isInfoNameResidency() {
        return infoNameResidency;
    }

    public void setInfoNameResidency(boolean infoNameResidency) {
        this.infoNameResidency = infoNameResidency;
    }

    public boolean isInfoBankAddress() {
        return infoBankAddress;
    }

    public void setInfoBankAddress(boolean infoBankAddress) {
        this.infoBankAddress = infoBankAddress;
    }

    public boolean isInfoEducationDebt() {
        return infoEducationDebt;
    }

    public void setInfoEducationDebt(boolean infoEducationDebt) {
        this.infoEducationDebt = infoEducationDebt;
    }

    public boolean isIncomeWagesSalary() {
        return incomeWagesSalary;
    }

    public void setIncomeWagesSalary(boolean incomeWagesSalary) {
        this.incomeWagesSalary = incomeWagesSalary;
    }

    public boolean isIncomeGovPayments() {
        return incomeGovPayments;
    }

    public void setIncomeGovPayments(boolean incomeGovPayments) {
        this.incomeGovPayments = incomeGovPayments;
    }

    public boolean isIncomeBankInterests() {
        return incomeBankInterests;
    }

    public void setIncomeBankInterests(boolean incomeBankInterests) {
        this.incomeBankInterests = incomeBankInterests;
    }

    public boolean isIncomeDividends() {
        return incomeDividends;
    }

    public void setIncomeDividends(boolean incomeDividends) {
        this.incomeDividends = incomeDividends;
    }

    public boolean isIncomeEtps() {
        return incomeEtps;
    }

    public void setIncomeEtps(boolean incomeEtps) {
        this.incomeEtps = incomeEtps;
    }

    public boolean isIncomeAnnuities() {
        return incomeAnnuities;
    }

    public void setIncomeAnnuities(boolean incomeAnnuities) {
        this.incomeAnnuities = incomeAnnuities;
    }

    public boolean isIncomeLumpSums() {
        return incomeLumpSums;
    }

    public void setIncomeLumpSums(boolean incomeLumpSums) {
        this.incomeLumpSums = incomeLumpSums;
    }

    public boolean isIncomeRentals() {
        return incomeRentals;
    }

    public void setIncomeRentals(boolean incomeRentals) {
        this.incomeRentals = incomeRentals;
    }

    public boolean isDeductionVehicles() {
        return deductionVehicles;
    }

    public void setDeductionVehicles(boolean deductionVehicles) {
        this.deductionVehicles = deductionVehicles;
    }

    public boolean isDeductionClothes() {
        return deductionClothes;
    }

    public void setDeductionClothes(boolean deductionClothes) {
        this.deductionClothes = deductionClothes;
    }

    public boolean isDeductionEducations() {
        return deductionEducations;
    }

    public void setDeductionEducations(boolean deductionEducations) {
        this.deductionEducations = deductionEducations;
    }

    public boolean isDeductionOthers() {
        return deductionOthers;
    }

    public void setDeductionOthers(boolean deductionOthers) {
        this.deductionOthers = deductionOthers;
    }

    public boolean isDeductionDonations() {
        return deductionDonations;
    }

    public void setDeductionDonations(boolean deductionDonations) {
        this.deductionDonations = deductionDonations;
    }

    public boolean isDeductionTaxAgents() {
        return deductionTaxAgents;
    }

    public void setDeductionTaxAgents(boolean deductionTaxAgents) {
        this.deductionTaxAgents = deductionTaxAgents;
    }

    public boolean isHealthDependants() {
        return healthDependants;
    }

    public void setHealthDependants(boolean healthDependants) {
        this.healthDependants = healthDependants;
    }

    public boolean isHealthMedicares() {
        return healthMedicares;
    }

    public void setHealthMedicares(boolean healthMedicares) {
        this.healthMedicares = healthMedicares;
    }

    public boolean isHealthPrivates() {
        return healthPrivates;
    }

    public void setHealthPrivates(boolean healthPrivates) {
        this.healthPrivates = healthPrivates;
    }

    public boolean isHealthSpouses() {
        return healthSpouses;
    }

    public void setHealthSpouses(boolean healthSpouses) {
        this.healthSpouses = healthSpouses;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "ReviewProgressResponse{" +
                "infoNameResidency=" + infoNameResidency +
                ", infoBankAddress=" + infoBankAddress +
                ", infoEducationDebt=" + infoEducationDebt +
                ", incomeWagesSalary=" + incomeWagesSalary +
                ", incomeGovPayments=" + incomeGovPayments +
                ", incomeBankInterests=" + incomeBankInterests +
                ", incomeDividends=" + incomeDividends +
                ", incomeEtps=" + incomeEtps +
                ", incomeAnnuities=" + incomeAnnuities +
                ", incomeLumpSums=" + incomeLumpSums +
                ", incomeRentals=" + incomeRentals +
                ", deductionVehicles=" + deductionVehicles +
                ", deductionClothes=" + deductionClothes +
                ", deductionEducations=" + deductionEducations +
                ", deductionOthers=" + deductionOthers +
                ", deductionDonations=" + deductionDonations +
                ", deductionTaxAgents=" + deductionTaxAgents +
                ", healthDependants=" + healthDependants +
                ", healthMedicares=" + healthMedicares +
                ", healthPrivates=" + healthPrivates +
                ", healthSpouses=" + healthSpouses +
                ", total=" + total +
                ", complete=" + complete +
                ", percent=" + percent +
                '}';
    }
}
