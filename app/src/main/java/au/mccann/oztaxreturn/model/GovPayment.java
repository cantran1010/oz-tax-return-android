package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by CanTran on 4/23/18.
 */
public class GovPayment implements Serializable {
    @SerializedName("had")
    private boolean had;
    @SerializedName("income_type")
    private String type;
    @SerializedName("gross_income")
    private String gross;
    @SerializedName("tax_withheld")
    private String tax;
    @SerializedName("attachments")
    private ArrayList<Attachment> attachments;

    public boolean isHad() {
        return had;
    }

    public void setHad(boolean had) {
        this.had = had;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGross() {
        return gross;
    }

    public void setGross(String gross) {
        this.gross = gross;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "GovPayment{" +
                "had=" + had +
                ", type='" + type + '\'' +
                ", gross='" + gross + '\'' +
                ", tax='" + tax + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
