package au.mccann.oztaxreturn.model;

import java.util.ArrayList;

/**
 * Created by CanTran on 5/4/18.
 */
public class OtherResponse {
    private int id;
    private String type;
    private String description;
    private String amount;
    private ArrayList<Attachment> attachments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "OtherResponse{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", amount='" + amount + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
