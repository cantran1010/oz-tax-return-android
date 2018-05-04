package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by CanTran on 5/4/18.
 */
public class Vehicles {
    private boolean had;
    @SerializedName("how_related")
    private String howRelated;
    @SerializedName("km_travelled")
    private String travelled;
    @SerializedName("type_brand")
    private String typeBrand;
    @SerializedName("reg_number")
    private String regNumber;
    @SerializedName("amount")
    private String amount;
    private ArrayList<Attachment> attachments;

    public boolean isHad() {
        return had;
    }

    public void setHad(boolean had) {
        this.had = had;
    }

    public String getHowRelated() {
        return howRelated;
    }

    public void setHowRelated(String howRelated) {
        this.howRelated = howRelated;
    }

    public String getTravelled() {
        return travelled;
    }

    public void setTravelled(String travelled) {
        this.travelled = travelled;
    }

    public String getTypeBrand() {
        return typeBrand;
    }

    public void setTypeBrand(String typeBrand) {
        this.typeBrand = typeBrand;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "Vehicles{" +
                "had=" + had +
                ", howRelated='" + howRelated + '\'' +
                ", travelled='" + travelled + '\'' +
                ", typeBrand='" + typeBrand + '\'' +
                ", regNumber='" + regNumber + '\'' +
                ", amount='" + amount + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
