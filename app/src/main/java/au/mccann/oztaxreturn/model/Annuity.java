package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by CanTran on 4/23/18.
 */
public class Annuity {
    private String id;
    @SerializedName("tax_withheld")
    private String taxWithheld;
    @SerializedName("taxable_com_taxed")
    private String taxableComTaxed;
    @SerializedName("taxable_com_untaxed")
    private String taxableComUntaxed;
    @SerializedName("arrears_taxed")
    private String arrearsTaxed;
    @SerializedName("arrears_untaxed")
    private String arrearsUntaxed;
    @SerializedName("attachments")
    private ArrayList<Attachment> attachments;
    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<Attachment> attach = new ArrayList<>();
    private ArrayList<Image> listUp = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getArrearsTaxed() {
        return arrearsTaxed;
    }

    public void setArrearsTaxed(String arrearsTaxed) {
        this.arrearsTaxed = arrearsTaxed;
    }

    public String getArrearsUntaxed() {
        return arrearsUntaxed;
    }

    public void setArrearsUntaxed(String arrearsUntaxed) {
        this.arrearsUntaxed = arrearsUntaxed;
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
        return "Annuity{" +
                "id='" + id + '\'' +
                ", taxWithheld='" + taxWithheld + '\'' +
                ", taxableComTaxed='" + taxableComTaxed + '\'' +
                ", taxableComUntaxed='" + taxableComUntaxed + '\'' +
                ", arrearsTaxed='" + arrearsTaxed + '\'' +
                ", arrearsUntaxed='" + arrearsUntaxed + '\'' +
                ", attachments=" + attachments +
                ", images=" + images +
                ", attach=" + attach +
                ", listUp=" + listUp +
                '}';
    }
}
