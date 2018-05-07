package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by CanTran on 5/7/18.
 */
public class IncomePart {
    @SerializedName("wages_salary")
    private String salary;
    @SerializedName("gov_payments")
    private String govPayments;
    @SerializedName("bank_interests")
    private String bankInterests;
    private String dividends;
    private String etps;
    @SerializedName("super_income_stream")
    private String superIncomeStream;
    @SerializedName("super_lump_sum")
    private String superLumpSum;
    private String rentals;

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getGovPayments() {
        return govPayments;
    }

    public void setGovPayments(String govPayments) {
        this.govPayments = govPayments;
    }

    public String getBankInterests() {
        return bankInterests;
    }

    public void setBankInterests(String bankInterests) {
        this.bankInterests = bankInterests;
    }

    public String getDividends() {
        return dividends;
    }

    public void setDividends(String dividends) {
        this.dividends = dividends;
    }

    public String getEtps() {
        return etps;
    }

    public void setEtps(String etps) {
        this.etps = etps;
    }

    public String getSuperIncomeStream() {
        return superIncomeStream;
    }

    public void setSuperIncomeStream(String superIncomeStream) {
        this.superIncomeStream = superIncomeStream;
    }

    public String getSuperLumpSum() {
        return superLumpSum;
    }

    public void setSuperLumpSum(String superLumpSum) {
        this.superLumpSum = superLumpSum;
    }

    public String getRentals() {
        return rentals;
    }

    public void setRentals(String rentals) {
        this.rentals = rentals;
    }

    @Override
    public String toString() {
        return "Income{" +
                "salary='" + salary + '\'' +
                ", govPayments='" + govPayments + '\'' +
                ", bankInterests='" + bankInterests + '\'' +
                ", dividends='" + dividends + '\'' +
                ", etps='" + etps + '\'' +
                ", superIncomeStream='" + superIncomeStream + '\'' +
                ", superLumpSum='" + superLumpSum + '\'' +
                ", rentals='" + rentals + '\'' +
                '}';
    }
}
