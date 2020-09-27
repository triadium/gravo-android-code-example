package ru.gravo.base;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Алексей on 28.10.2017.
 */

public class ResponseBox {
    @SerializedName("ok")
    @Expose
    private Boolean ok;
    @SerializedName("redirect")
    @Expose
    private Boolean redirect;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("reason")
    @Expose
    private String reason;

    public Boolean isOk(){
        return (ok != null && ok);
    }

    public Boolean isRedirect(){
        return (redirect != null && redirect);
    }

    public String getUrl(){
        return url;
    }

    public String getReason() {
        return reason;
    }
}
