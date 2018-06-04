package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by CanTran on 6/4/18.
 */
public class BaseAppProgress {
    private boolean income;
    @SerializedName("income_others")
    private boolean other;
    private boolean deduction;
    @SerializedName("personal_information")
    private boolean personal;
    @SerializedName("bank_details")
    private boolean bank;
    private boolean checkout;
    private int total;
    private int complete;
    private int percent;

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }

    public boolean isOther() {
        return other;
    }

    public void setOther(boolean other) {
        this.other = other;
    }

    public boolean isDeduction() {
        return deduction;
    }

    public void setDeduction(boolean deduction) {
        this.deduction = deduction;
    }

    public boolean isPersonal() {
        return personal;
    }

    public void setPersonal(boolean personal) {
        this.personal = personal;
    }

    public boolean isBank() {
        return bank;
    }

    public void setBank(boolean bank) {
        this.bank = bank;
    }

    public boolean isCheckout() {
        return checkout;
    }

    public void setCheckout(boolean checkout) {
        this.checkout = checkout;
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
        return "BaseAppProgress{" +
                "income=" + income +
                ", other=" + other +
                ", deduction=" + deduction +
                ", personal=" + personal +
                ", bank=" + bank +
                ", checkout=" + checkout +
                ", total=" + total +
                ", complete=" + complete +
                ", percent=" + percent +
                '}';
    }
}
