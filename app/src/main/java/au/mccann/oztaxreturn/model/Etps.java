package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by CanTran on 4/23/18.
 */
public class Etps {
    private boolean had;
    @SerializedName("payment_date")
    private String paymentDate;
    @SerializedName("payer_abn")
    private String payerAbn;
    @SerializedName("tax_withheld")
    private String taxWithheld;
    @SerializedName("taxable_com")
    private String taxableCom;
    @SerializedName("code")
    private String code;
    @SerializedName("attachments")
    private ArrayList<Attachment> attachments;

    public boolean isHad() {
        return had;
    }

    public void setHad(boolean had) {
        this.had = had;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPayerAbn() {
        return payerAbn;
    }

    public void setPayerAbn(String payerAbn) {
        this.payerAbn = payerAbn;
    }

    public String getTaxWithheld() {
        return taxWithheld;
    }

    public void setTaxWithheld(String taxWithheld) {
        this.taxWithheld = taxWithheld;
    }

    public String getTaxableCom() {
        return taxableCom;
    }

    public void setTaxableCom(String taxableCom) {
        this.taxableCom = taxableCom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "Etps{" +
                "had=" + had +
                ", paymentDate='" + paymentDate + '\'' +
                ", payerAbn='" + payerAbn + '\'' +
                ", taxWithheld='" + taxWithheld + '\'' +
                ", taxableCom='" + taxableCom + '\'' +
                ", code='" + code + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
