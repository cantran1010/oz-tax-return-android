package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by CanTran on 4/26/18.
 */
public class ResponseBasicInformation implements Serializable {
    private int appId;
    @SerializedName("income_wages_salary")
    private WagesSalary incomeWagesSalary;
    @SerializedName("income_others")
    private Other other;
    @SerializedName("deduction")
    private Deduction deduction;
    @SerializedName("personal_information")
    private PersonalInformation personalInformation;


    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public WagesSalary getIncomeWagesSalary() {
        return incomeWagesSalary;
    }

    public void setIncomeWagesSalary(WagesSalary incomeWagesSalary) {
        this.incomeWagesSalary = incomeWagesSalary;
    }

    public Other getOther() {
        return other;
    }

    public void setOther(Other other) {
        this.other = other;
    }

    public Deduction getDeduction() {
        return deduction;
    }

    public void setDeduction(Deduction deduction) {
        this.deduction = deduction;
    }

    public PersonalInformation getPersonalInformation() {
        return personalInformation;
    }

    public void setPersonalInformation(PersonalInformation personalInformation) {
        this.personalInformation = personalInformation;
    }

    @Override
    public String toString() {
        return "ResponseBasicInformation{" +
                "appId=" + appId +
                ", incomeWagesSalary=" + incomeWagesSalary +
                ", other=" + other +
                ", deduction=" + deduction +
                ", personalInformation=" + personalInformation +
                '}';
    }
}
