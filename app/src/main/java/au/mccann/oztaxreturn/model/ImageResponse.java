package au.mccann.oztaxreturn.model;

/**
 * Created by LongBui on 5/9/2017.
 */

public class ImageResponse {

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
        return "ImageResponse{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }

}
