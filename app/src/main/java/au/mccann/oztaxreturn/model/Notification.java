package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import au.mccann.oztaxreturn.rest.response.ApplicationResponse;

/**
 * Created by LongBui on 5/8/18.
 */
public class Notification {
    private int id;
    private String content;
    private String event;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("app")
    private ApplicationResponse application;

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
                '}';
    }

}
