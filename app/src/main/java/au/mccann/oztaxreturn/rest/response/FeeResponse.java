package au.mccann.oztaxreturn.rest.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by LongBui on 5/2/18.
 */

public class FeeResponse implements Serializable{
    private int amount;
    @SerializedName("amount_after")
    private int amountAfter;
    @SerializedName("amount_discounted")
    private int amountDiscounted;
    private String currency;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmountAfter() {
        return amountAfter;
    }

    public void setAmountAfter(int amountAfter) {
        this.amountAfter = amountAfter;
    }

    public int getAmountDiscounted() {
        return amountDiscounted;
    }

    public void setAmountDiscounted(int amountDiscounted) {
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
