package au.mccann.oztaxreturn.model;

/**
 * Created by CanTran on 5/7/18.
 */
public class IncomeSummary {
    private IncomePart parts;
    private String total;

    public IncomePart getParts() {
        return parts;
    }

    public void setParts(IncomePart parts) {
        this.parts = parts;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "IncomeSummary{" +
                "parts=" + parts +
                ", total='" + total + '\'' +
                '}';
    }
}
