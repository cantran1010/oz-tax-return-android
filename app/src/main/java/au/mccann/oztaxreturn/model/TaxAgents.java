package au.mccann.oztaxreturn.model;

import java.util.ArrayList;

/**
 * Created by CanTran on 5/4/18.
 */
public class TaxAgents {
    private boolean had;
    private String organization;
    private String amount;
    private ArrayList<Attachment> attachments;

    public boolean isHad() {
        return had;
    }

    public void setHad(boolean had) {
        this.had = had;
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
        return "TaxAgents{" +
                "had=" + had +
                ", organization='" + organization + '\'' +
                ", amount='" + amount + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
