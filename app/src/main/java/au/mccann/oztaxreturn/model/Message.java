package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LongBui on 5/8/18.
 */
public class Message {

    @SerializedName("id")
    public int id;
    @SerializedName("sender_id")
    public int senderId;
    @SerializedName("first_name")
    public String firstName;
    @SerializedName("middle_name")
    public String middleName;
    @SerializedName("last_name")
    public String lastName;
    @SerializedName("avatar_url")
    public String avatarUrl;
    @SerializedName("message")
    public String message;
    @SerializedName("is_manager")
    public boolean isManager;
    @SerializedName("created_at")
    public String createdAt;
    private Attachment attachment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", message='" + message + '\'' +
                ", isManager=" + isManager +
                ", createdAt='" + createdAt + '\'' +
                ", attachment=" + attachment +
                '}';
    }

}
