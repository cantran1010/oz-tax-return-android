package au.mccann.oztaxreturn.model;

/**
 * Created by CanTran on 5/7/18.
 */
public class DeductionSummary {
    private DeductionPart parts;
    private String total;

    public DeductionPart getParts() {
        return parts;
    }

    public void setParts(DeductionPart parts) {
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
        return "DeductionSummary{" +
                "parts=" + parts +
                ", total='" + total + '\'' +
                '}';
    }
}
