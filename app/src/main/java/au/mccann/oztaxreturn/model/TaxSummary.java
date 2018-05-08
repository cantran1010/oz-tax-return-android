package au.mccann.oztaxreturn.model;

/**
 * Created by CanTran on 5/7/18.
 */
public class TaxSummary {
    private TaxPart parts;
    private String total;

    public TaxPart getParts() {
        return parts;
    }

    public void setParts(TaxPart parts) {
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
        return "TaxSummary{" +
                "parts=" + parts +
                ", total='" + total + '\'' +
                '}';
    }
}
