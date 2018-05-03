package au.mccann.oztaxreturn.rest.response;

import java.util.List;

import au.mccann.oztaxreturn.model.Attachment;

/**
 * Created by LongBui on 5/3/18.
 */

public class MedicareResponse {
    private boolean had;
    private List<Attachment> attachments;

    public boolean isHad() {
        return had;
    }

    public void setHad(boolean had) {
        this.had = had;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "MedicareResponse{" +
                "had=" + had +
                ", attachments=" + attachments +
                '}';
    }

}
