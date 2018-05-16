package au.mccann.oztaxreturn.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by LongBui on 5/2/18.
 */

public class FeeResponse implements Serializable {
    private float amount;
    @SerializedName("amount_after")
    private float amountAfter;
    @SerializedName("amount_discounted")
    private float amountDiscounted;
    private String currency;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getAmountAfter() {
        return amountAfter;
    }

    public void setAmountAfter(float amountAfter) {
        this.amountAfter = amountAfter;
    }

    public float getAmountDiscounted() {
        return amountDiscounted;
    }

    public void setAmountDiscounted(float amountDiscounted) {
        this.amountDiscounted = amountDiscounted;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "FeeResponse{" +
                "amount=" + amount +
                ", amountAfter=" + amountAfter +
                ", amountDiscounted=" + amountDiscounted +
                ", currency='" + currency + '\'' +
                '}';
    }

}
