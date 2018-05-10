package au.mccann.oztaxreturn.database;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by CanTran on 5/9/18.
 */
public class AvatarEntity extends RealmObject implements Serializable {
    private String id;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        return "AvatarEntity{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
