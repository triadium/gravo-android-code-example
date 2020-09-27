package ru.gravo.api;

import java.util.HashMap;

import ru.gravo.boxes.BpoA;
import ru.gravo.boxes.BpoLc;
import ru.gravo.boxes.work_place_list.APlace;
import ru.gravo.boxes.work_place_list.LcCfgWorkPlace;
import ru.gravo.boxes.work_place_list.WorkPlaceListYota;
import ru.gravo.utils.BitHelper;

/**
 * Created by Алексей on 27.11.2017.
 */

public final class DataTransform {
    public static HashMap<String, WorkPlaceListYota> genWorkPlaceMap(BpoLc<LcCfgWorkPlace> data){
        BpoA<APlace>[] places = data.getLc().getCfgWorkPlaceList();
        HashMap<String, WorkPlaceListYota> map = new HashMap<>(places.length);
        for (int i = 0; i < places.length; i++) {
            WorkPlaceListYota placeYota = new WorkPlaceListYota(places[i]);
            placeYota.getA().setParsedColor(BitHelper.parseColor(placeYota.getA().getColor()));
            map.put(places[i].getKey(), placeYota);
        }

        return map;
    }
}
