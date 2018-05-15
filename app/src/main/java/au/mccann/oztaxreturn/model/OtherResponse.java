package au.mccann.oztaxreturn.model;

import java.util.ArrayList;

/**
 * Created by CanTran on 5/4/18.
 */
public class OtherResponse {
    private int id;
    private String type="";
    private String description;
    private String amount;
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
        return "OtherResponse{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", amount='" + amount + '\'' +
                ", attachments=" + attachments +
                ", images=" + images +
                ", attach=" + attach +
                ", listUp=" + listUp +
                '}';
    }
}
