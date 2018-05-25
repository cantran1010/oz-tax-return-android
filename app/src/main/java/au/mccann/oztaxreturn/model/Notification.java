package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import au.mccann.oztaxreturn.rest.response.ApplicationResponse;

/**
 * Created by LongBui on 5/8/18.
 */
public class Notification implements Serializable {
    private int id;
    private String content;
    private String event;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("app")
    private ApplicationResponse application;
    @SerializedName("manager_id")
    private String managerId;
    @SerializedName("manager_avatar")
    private String managerAvatar;

    public String getManagerAvatar() {
        return managerAvatar;
    }

    public void setManagerAvatar(String managerAvatar) {
        this.managerAvatar = managerAvatar;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ApplicationResponse getApplication() {
        return application;
    }

    public void setApplication(ApplicationResponse application) {
        this.application = application;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", event='" + event + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", application=" + application +
                ", managerId='" + managerId + '\'' +
                ", managerAvatar='" + managerAvatar + '\'' +
                '}';
    }

}
