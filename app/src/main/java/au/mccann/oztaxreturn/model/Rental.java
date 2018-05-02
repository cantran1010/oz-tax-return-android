package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by CanTran on 4/23/18.
 */
public class Rental {
    private boolean had;
    @SerializedName("ownership_per")
    private String ownership;
    @SerializedName("street")
    private String street;
    @SerializedName("suburb")
    private String suburb;
    @SerializedName("state")
    private String state;
    @SerializedName("postcode")
    private String postcode;
    @SerializedName("first_earned")
    private String firstEarned;
    @SerializedName("income")
    private String income;
    @SerializedName("expenses")
    private String expenses;
    @SerializedName("attachments")
    private ArrayList<Attachment> attachments;

    public boolean isHad() {
        return had;
    }

    public void setHad(boolean had) {
        this.had = had;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getFirstEarned() {
        return firstEarned;
    }

    public void setFirstEarned(String firstEarned) {
        this.firstEarned = firstEarned;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getExpenses() {
        return expenses;
    }

    public void setExpenses(String expenses) {
        this.expenses = expenses;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "Rental{" +
                "had=" + had +
                ", ownership='" + ownership + '\'' +
                ", street='" + street + '\'' +
                ", suburb='" + suburb + '\'' +
                ", state='" + state + '\'' +
                ", postcode='" + postcode + '\'' +
                ", firstEarned='" + firstEarned + '\'' +
                ", income='" + income + '\'' +
                ", expenses='" + expenses + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
