package au.mccann.oztaxreturn.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by CanTran on 4/26/18.
 */
public class Other implements Serializable {
    private boolean had;
    @SerializedName("content")
    private String content;
    private List<Attachment> attachments;

    public boolean isHad() {
        return had;
    }

    public void setHad(boolean had) {
        this.had = had;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "Other{" +
                "had=" + had +
                ", content='" + content + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
