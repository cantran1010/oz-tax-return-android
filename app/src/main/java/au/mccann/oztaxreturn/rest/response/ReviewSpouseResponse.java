package au.mccann.oztaxreturn.rest.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.mccann.oztaxreturn.model.Attachment;

/**
 * Created by LongBui on 5/4/18.
 */

public class ReviewSpouseResponse {

    @SerializedName("had")
    private Boolean had;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("middle_name")
    private String middleName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("taxable_income")
    private double taxableIncome;
    private List<Attachment> attachments;

    public Boolean getHad() {
        return had;
    }

    public void setHad(Boolean had) {
        this.had = had;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public double getTaxableIncome() {
        return taxableIncome;
    }

    public void setTaxableIncome(double taxableIncome) {
        this.taxableIncome = taxableIncome;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "ReviewSpouseResponse{" +
                "had=" + had +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthday='" + birthday + '\'' +
                ", taxableIncome=" + taxableIncome +
                ", attachments=" + attachments +
                '}';
    }

}
