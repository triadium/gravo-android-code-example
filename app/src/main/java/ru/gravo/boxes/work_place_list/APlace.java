package ru.gravo.boxes.work_place_list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Алексей on 02.11.2017.
 */

public class APlace {
    @SerializedName("plc_a")
    @Expose
    private String alias;

    @SerializedName("plc_c")
    @Expose
    private String color;

    @SerializedName("plc_c=>")
    @Expose
    private int parsedColor;

    public String getAlias() {
        return alias;
    }

    public String getColor() {
        return color;
    }

    public void setParsedColor(int value) {
        parsedColor = value;
    }

    public int getParsedColor() {
        return parsedColor;
    }
}
