package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by CanTran on 5/5/18.
 */
public class LumpSum implements Serializable {
    private int id;
    @SerializedName("payer_abn")
    private String payerAbn;
    @SerializedName("tax_withheld")
    private String taxWithheld;
    @SerializedName("taxable_com_taxed")
    private String taxableComTaxed;
    @SerializedName("taxable_com_untaxed")
    private String taxableComUntaxed;
    @SerializedName("payment_date")
    private String paymentDate;
    private ArrayList<Attachment> attachments;
    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<Attachment> attach = new ArrayList<>();
    private ArrayList<Image> listUp = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTaxableComTaxed() {
        return taxableComTaxed;
    }

    public void setTaxableComTaxed(String taxableComTaxed) {
        this.taxableComTaxed = taxableComTaxed;
    }

    public String getTaxableComUntaxed() {
        return taxableComUntaxed;
    }

    public void setTaxableComUntaxed(String taxableComUntaxed) {
        this.taxableComUntaxed = taxableComUntaxed;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public ArrayList<Attachment> getAttach() {
        return attach;
    }

    public void setAttach(ArrayList<Attachment> attach) {
        this.attach = attach;
    }

    public ArrayList<Image> getListUp() {
        return listUp;
    }

    public void setListUp(ArrayList<Image> listUp) {
        this.listUp = listUp;
    }

    @Override
    public String toString() {
        return "LumpSum{" +
                "id=" + id +
                ", payerAbn='" + payerAbn + '\'' +
                ", taxWithheld='" + taxWithheld + '\'' +
                ", taxableComTaxed='" + taxableComTaxed + '\'' +
                ", taxableComUntaxed='" + taxableComUntaxed + '\'' +
                ", paymentDate='" + paymentDate + '\'' +
                ", attachments=" + attachments +
                ", images=" + images +
                ", attach=" + attach +
                ", listUp=" + listUp +
                '}';
    }
}
