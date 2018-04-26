package au.mccann.oztaxreturn.model;

import java.io.Serializable;

/**
 * Created by CanTran on 4/23/18.
 */
public class Attachment implements Serializable {
    private int id;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }
}
