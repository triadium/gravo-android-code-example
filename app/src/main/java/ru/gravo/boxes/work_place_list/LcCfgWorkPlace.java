package ru.gravo.boxes.work_place_list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ru.gravo.boxes.BpoA;

/**
 * Created by Алексей on 02.11.2017.
 */

public class LcCfgWorkPlace {
    @SerializedName("CFG.WP")
    @Expose
    private BpoA<APlace>[] cfgWorkPlaceList;

    public BpoA<APlace>[] getCfgWorkPlaceList() {
        return cfgWorkPlaceList;
    }
}
