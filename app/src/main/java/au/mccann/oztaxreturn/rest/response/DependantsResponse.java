package au.mccann.oztaxreturn.rest.response;

/**
 * Created by LongBui on 5/3/18.
 */

public class DependantsResponse {
    private boolean had;
    private int number;

    public boolean isHad() {
        return had;
    }

    public void setHad(boolean had) {
        this.had = had;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "DependantsResponse{" +
                "had=" + had +
                ", number=" + number +
                '}';
    }
}
