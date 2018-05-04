package au.mccann.oztaxreturn.model;

import java.util.ArrayList;

/**
 * Created by CanTran on 5/4/18.
 */
public class Donation {
    private int id;
    private String organization;
    private String amount;
    private ArrayList<Attachment> attachments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
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
        return "Donation{" +
                "id=" + id +
                ", organization='" + organization + '\'' +
                ", amount='" + amount + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
