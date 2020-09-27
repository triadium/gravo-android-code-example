package ru.gravo.boxes.work_place_list;

import ru.gravo.boxes.BpoA;

/**
 * Created by Алексей on 03.11.2017.
 */

//FIXME: Backed class from generic one for using with Gson
public class WorkPlaceListYota extends BpoA<APlace> {
    public WorkPlaceListYota(BpoA<APlace> src){
        super();
        super.setKey(src.getKey());
        super.setA(src.getA());
    }
}
