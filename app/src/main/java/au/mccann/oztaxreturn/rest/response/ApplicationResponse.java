package au.mccann.oztaxreturn.rest.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by LongBui on 4/18/18.
 */

public class ApplicationResponse implements Parcelable,Serializable {
    private int id;
    @SerializedName("payer_name")
    private String payerName;
    @SerializedName("financial_year")
    private String financialYear;

    public ApplicationResponse(Parcel in) {
        id = in.readInt();
        payerName = in.readString();
        financialYear = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(payerName);
        dest.writeString(financialYear);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ApplicationResponse> CREATOR = new Creator<ApplicationResponse>() {
        @Override
        public ApplicationResponse createFromParcel(Parcel in) {
            return new ApplicationResponse(in);
        }

        @Override
        public ApplicationResponse[] newArray(int size) {
            return new ApplicationResponse[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    @Override
    public String toString() {
        return "ApplicationResponse{" +
                "id=" + id +
                ", payerName='" + payerName + '\'' +
                ", financialYear='" + financialYear + '\'' +
                '}';
    }

}
