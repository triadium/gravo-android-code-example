package ru.gravo.base;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Алексей on 02.11.2017.
 */

public class CapsuleBox<D, P> extends ResponseBox {
    @SerializedName("d")
    @Expose
    private D data;

    @SerializedName("p")
    @Expose
    private P parameters;

    public D getData() {
        return data;
    }

    public P getParameters() {
        return parameters;
    }
}
