package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by CanTran on 4/23/18.
 */
public class Etps {
    @SerializedName("payment_date")
    private String payment_date;
    @SerializedName("payer_abn")
    private String payer_abn;
    @SerializedName("tax_withheld")
    private String tax_withheld;
    @SerializedName("taxable_com")
    private String taxable_com;
    @SerializedName("code")
    private String code;
    @SerializedName("attachments")
    private ArrayList<Attachment> attachments;

    public String getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(String payment_date) {
        this.payment_date = payment_date;
    }

    public String getPayer_abn() {
        return payer_abn;
    }

    public void setPayer_abn(String payer_abn) {
        this.payer_abn = payer_abn;
    }

    public String getTax_withheld() {
        return tax_withheld;
    }

    public void setTax_withheld(String tax_withheld) {
        this.tax_withheld = tax_withheld;
    }

    public String getTaxable_com() {
        return taxable_com;
    }

    public void setTaxable_com(String taxable_com) {
        this.taxable_com = taxable_com;
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
                "payment_date='" + payment_date + '\'' +
                ", payer_abn='" + payer_abn + '\'' +
                ", tax_withheld='" + tax_withheld + '\'' +
                ", taxable_com='" + taxable_com + '\'' +
                ", code='" + code + '\'' +
                ", attachments='" + attachments + '\'' +
                '}';
    }
}
