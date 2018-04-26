package au.mccann.oztaxreturn.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by CanTran on 4/26/18.
 */
public class Deduction implements Serializable {
    private String content;
    private List<Attachment> attachments;

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
        return "Deduction{" +
                "content='" + content + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
