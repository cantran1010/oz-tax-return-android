package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by CanTran on 4/23/18.
 */
public class Dividend {
    private String id;
    @SerializedName("company_name")
    private String companyName;
    @SerializedName("unfranked")
    private String unfranked;
    @SerializedName("franked")
    private String franked;
    @SerializedName("franking_credits")
    private String frankingCredits;
    @SerializedName("tax_withheld")
    private String taxWithheld;
    @SerializedName("attachments")
    private ArrayList<Attachment> attachments;
    private ArrayList<Image> images;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUnfranked() {
        return unfranked;
    }

    public void setUnfranked(String unfranked) {
        this.unfranked = unfranked;
    }

    public String getFranked() {
        return franked;
    }

    public void setFranked(String franked) {
        this.franked = franked;
    }

    public String getFrankingCredits() {
        return frankingCredits;
    }

    public void setFrankingCredits(String frankingCredits) {
        this.frankingCredits = frankingCredits;
    }

    public String getTaxWithheld() {
        return taxWithheld;
    }

    public void setTaxWithheld(String taxWithheld) {
        this.taxWithheld = taxWithheld;
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

    @Override
    public String toString() {
        return "Dividend{" +
                "id='" + id + '\'' +
                ", companyName='" + companyName + '\'' +
                ", unfranked='" + unfranked + '\'' +
                ", franked='" + franked + '\'' +
                ", frankingCredits='" + frankingCredits + '\'' +
                ", taxWithheld='" + taxWithheld + '\'' +
                ", attachments=" + attachments +
                ", images=" + images +
                '}';
    }
}
