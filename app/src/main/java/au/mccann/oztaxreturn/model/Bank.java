package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by CanTran on 4/23/18.
 */
public class Bank {
    @SerializedName("bank_name")
    private String bankName;
    @SerializedName("account_number")
    private String accountNumber;
    @SerializedName("total_interest")
    private String totalInterest;
    @SerializedName("tax_withheld")
    private String taxWithheld;
    @SerializedName("fees")
    private String fees;
    @SerializedName("attachments")
    private ArrayList<Attachment> attachments;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(String totalInterest) {
        this.totalInterest = totalInterest;
    }

    public String getTaxWithheld() {
        return taxWithheld;
    }

    public void setTaxWithheld(String taxWithheld) {
        this.taxWithheld = taxWithheld;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "Bank{" +
                "bankName='" + bankName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", totalInterest='" + totalInterest + '\'' +
                ", taxWithheld='" + taxWithheld + '\'' +
                ", fees='" + fees + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
