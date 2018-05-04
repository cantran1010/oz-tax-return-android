package au.mccann.oztaxreturn.model;

import java.util.ArrayList;

/**
 * Created by CanTran on 5/4/18.
 */
public class Clothes {
    private boolean had;
    private String type;
    private String amount;
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
        return "Clothes{" +
                "had=" + had +
                ", type='" + type + '\'' +
                ", amount='" + amount + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
