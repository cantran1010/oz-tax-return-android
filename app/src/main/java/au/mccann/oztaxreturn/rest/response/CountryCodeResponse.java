package au.mccann.oztaxreturn.rest.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LongBui on 5/24/18.
 */
public class CountryCodeResponse {
    private String name;
    @SerializedName("dial_code")
    private String dialCode;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CountryCodeResponse{" +
                "name='" + name + '\'' +
                ", dialCode='" + dialCode + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

}
